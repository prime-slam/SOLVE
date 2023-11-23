package solve.rendering.engine.rendering.renderers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import solve.constants.ShadersFrameFragmentPath
import solve.constants.ShadersFrameGeometryPath
import solve.constants.ShadersFrameVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.rendering.batch.PrimitiveType
import solve.rendering.engine.rendering.batch.RenderBatch
import solve.rendering.engine.rendering.texture.ArrayTexture
import solve.rendering.engine.rendering.texture.Texture2D
import solve.rendering.engine.rendering.texture.Texture2DData
import solve.rendering.engine.rendering.texture.TextureChannelsType
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.structures.IntRect
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.toIntVector
import solve.scene.controller.SceneController
import solve.scene.model.VisualizationFrame
import solve.utils.ceilToInt
import tornadofx.*
import kotlin.math.abs

class FramesRenderer(
    window: Window,
    sceneController: SceneController
) : Renderer(window) {
    private data class LoadedBufferFrameData(val textureData: Texture2DData, val bufferIndex: Int)

    override val maxBatchSize = 1000
    private var modelsCommonMatrix = Matrix4f().identity()
    private var gridWidth = DefaultGridWidth
    private val gridHeight: Int
        get() = (frames.count().toFloat() / gridWidth).ceilToInt()
    private var buffersSize = defaultBuffersSize

    private var bufferFramesArrayTexture: ArrayTexture? = null

    private var frames = emptyList<VisualizationFrame>()
    private var framesWidth = 1
    private var framesHeight = 1
    private var framesChannelsType = TextureChannelsType.RGBA

    private var cameraLastGridCellPosition = getCameraGridCellPosition()

    private val bufferFramesToUpload = mutableListOf<LoadedBufferFrameData>()
    private val framesLoadingCoroutineScope = CoroutineScope(Dispatchers.Default)

    private var needToReinitializeBuffers = false

    // TODO: change scene frames from another class.
    init {
        sceneController.sceneProperty.onChange { scene ->
            scene ?: return@onChange
            changeSceneFrames(scene.frames)
        }
    }

    fun changeModelsCommonMatrix(newMatrix: Matrix4f) {
        modelsCommonMatrix = newMatrix
    }

    override fun createShaderProgram(): ShaderProgram {
        val shaderProgram = ShaderProgram()
        shaderProgram.addShader(ShadersFrameVertexPath, ShaderType.VERTEX)
        shaderProgram.addShader(ShadersFrameGeometryPath, ShaderType.GEOMETRY)
        shaderProgram.addShader(ShadersFrameFragmentPath, ShaderType.FRAGMENT)
        shaderProgram.link()

        return shaderProgram
    }

    override fun uploadUniforms(shaderProgram: ShaderProgram) {
        shaderProgram.uploadMatrix4f(ProjectionUniformName, window.calculateProjectionMatrix())
        shaderProgram.uploadMatrix4f(ModelUniformName, modelsCommonMatrix)
        shaderProgram.uploadInt(GridWidthUniformName, gridWidth)
        shaderProgram.uploadVector2i(BuffersSizeUniformName, buffersSize)
        shaderProgram.uploadInt(TexturesArrayUniformName, 0)
    }

    override fun createNewBatch(zIndex: Int) =
        RenderBatch(maxBatchSize, zIndex, PrimitiveType.Point, listOf(ShaderAttributeType.FLOAT))

    override fun updateBatchesData() {
        frames.forEachIndexed { index, _ ->
            val batch = getAvailableBatch(null, 0)
            batch.pushInt(index)
        }
    }

    override fun beforeRender() {
        if (frames.isEmpty()) {
            return
        }

        if (needToReinitializeBuffers) {
            reinitializeBuffers()
        }

        uploadLoadedFramesToBuffers()
        updateBuffersTextures()
    }

    private fun reinitializeBuffers() {
        bufferFramesArrayTexture?.delete()
        initializeTexturesBuffers(frames)
        needToReinitializeBuffers = false
    }

    private fun changeSceneFrames(frames: List<VisualizationFrame>) {
        if (frames.isEmpty()) {
            return
        }

        this.frames = frames
        needToReinitializeBuffers = true
    }

    private fun uploadLoadedFramesToBuffers() {
        bufferFramesToUpload.toList().forEach { frame ->
            bufferFramesArrayTexture?.uploadTexture(frame.textureData, frame.bufferIndex)
            bufferFramesToUpload.remove(frame)
            Texture2D.freeData(frame.textureData)
        }
    }

    private fun updateBuffersTextures() {
        val cameraGridCellPosition = getCameraGridCellPosition()
        if (cameraGridCellPosition != cameraLastGridCellPosition) {
            loadNewTexturesToBuffers(cameraGridCellPosition)
        }
        cameraLastGridCellPosition = cameraGridCellPosition
    }

    private fun loadNewTexturesToBuffers(cameraGridCellPosition: Vector2i) {
        val cameraPosition = Vector2i(cameraGridCellPosition)
        cameraPosition.x.coerceIn(0 until gridWidth)
        cameraPosition.y.coerceIn(0 until gridHeight)

        val gridCellPositionDelta = cameraPosition - cameraLastGridCellPosition
        if (gridCellPositionDelta == Vector2i(0)) {
            return
        }

        val rectWidth: Int
        val rectHeight: Int
        if (gridCellPositionDelta.x != 0) {
            rectWidth = abs(gridCellPositionDelta.x)
            rectHeight = buffersSize.y
        } else {
            rectHeight = abs(gridCellPositionDelta.y)
            rectWidth = buffersSize.x
        }
        val newFramesRect = IntRect(
            if (gridCellPositionDelta.x > 0) {
                cameraPosition.x + buffersSize.x - gridCellPositionDelta.x
            } else {
                cameraPosition.x
            },
            if (gridCellPositionDelta.y > 0) {
                cameraPosition.y + buffersSize.y - gridCellPositionDelta.y
            } else {
                cameraPosition.y
            },
            rectWidth,
            rectHeight
        )
        loadRectFramesToBuffers(newFramesRect)
    }

    private fun getFramesAtRect(rect: IntRect): List<List<VisualizationFrame>> {
        val framesRect = mutableListOf<List<VisualizationFrame>>()
        for (y in rect.y0 until rect.y0 + rect.height) {
            val framesFromIndex = (gridWidth * y + rect.x0).coerceIn(0..frames.lastIndex)
            val framesToIndex = (framesFromIndex + rect.width).coerceIn(0..frames.lastIndex)

            framesRect.add(frames.subList(framesFromIndex, framesToIndex))
        }

        return framesRect
    }

    private fun loadRectFramesToBuffers(framesRect: IntRect) {
        val rectFrames = getFramesAtRect(framesRect)

        if (rectFrames.isEmpty() || rectFrames.first().isEmpty()) {
            return
        }

        val buffersOffset = Vector2i(framesRect.x0 % buffersSize.x, framesRect.y0 % buffersSize.y)
        if (framesRect.width > buffersSize.x || framesRect.height > buffersSize.y) {
            println("The size of the loading frames is out of buffers bounds!")
            return
        }

        for (y in 0 until rectFrames.count()) {
            for (x in 0 until rectFrames[y].count()) {
                val textureBuffersIndex =
                    ((buffersOffset.y + y) % buffersSize.y) * buffersSize.x + (buffersOffset.x + x) % buffersSize.x
                uploadFrameToBuffersArray(rectFrames[y][x], textureBuffersIndex)
            }
        }
    }

    private fun getCameraGridCellPosition(): Vector2i {
        return (window.camera.position - Vector2f(buffersSize) / 2f).toIntVector()
    }

    private fun uploadInitialFramesToBuffer() {
        loadRectFramesToBuffers(IntRect(0, 0, buffersSize.x, buffersSize.y))
    }

    private fun initializeTexturesBuffers(frames: List<VisualizationFrame>) {
        val firstTextureData = Texture2D.loadData(frames.first().imagePath.toString())
        if (firstTextureData == null) {
            println("The read texture is null!")
            return
        }

        framesWidth = firstTextureData.width
        framesHeight = firstTextureData.height
        framesChannelsType = firstTextureData.channelsType

        bufferFramesArrayTexture =
            ArrayTexture(framesWidth, framesHeight, framesChannelsType, buffersSize.x * buffersSize.y)

        uploadInitialFramesToBuffer()
    }

    private fun uploadFrameToBuffersArray(frame: VisualizationFrame, index: Int) {
        framesLoadingCoroutineScope.launch {
            val textureData = Texture2D.loadData(frame.imagePath.toString())
            if (textureData == null) {
                println("The read texture is null!")
                return@launch
            }

            bufferFramesToUpload.add(LoadedBufferFrameData(textureData, index))
        }
    }

    companion object {
        private const val ProjectionUniformName = "uProjection"
        private const val ModelUniformName = "uModel"
        private const val GridWidthUniformName = "uGridWidth"
        private const val BuffersSizeUniformName = "uBuffersSize"
        private const val TexturesArrayUniformName = "uTextures"

        private const val DefaultGridWidth = 10

        private val defaultBuffersSize = Vector2i(4, 4)
    }
}