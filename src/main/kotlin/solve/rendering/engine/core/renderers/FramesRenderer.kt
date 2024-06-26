package solve.rendering.engine.core.renderers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import solve.constants.ShadersFrameFragmentPath
import solve.constants.ShadersFrameGeometryPath
import solve.constants.ShadersFrameVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.core.texture.ArrayTexture
import solve.rendering.engine.core.texture.Texture2D
import solve.rendering.engine.core.texture.Texture2DData
import solve.rendering.engine.core.texture.TextureChannelsType
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.structures.IntRect
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.toFloatVector
import solve.rendering.engine.utils.toIntVector
import solve.scene.controller.SceneController
import solve.scene.model.VisualizationFrame
import java.util.Date
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Used to draw the frames.
 * Encapsulates logic to provide virtualization at the data layer.*
 */
class FramesRenderer(
    window: Window
) : Renderer(window) {
    private data class LoadedBufferFrameData(
        val textureData: Texture2DData,
        val bufferIndex: Int,
        val time: Long
    )

    override val maxBatchSize = 1000
    private var modelsCommonMatrix = Matrix4f().identity()

    private var buffersSize = defaultBuffersSize

    private var bufferFramesArrayTexture: ArrayTexture? = null

    private var framesWidth = 1
    private var framesHeight = 1
    private var framesChannelsType = TextureChannelsType.RGBA

    private val framesRatio: Float
        get() = framesWidth.toFloat() / framesHeight.toFloat()

    private var cameraLastGridCellPosition = getScreenTopLeftGridCellPosition()

    private val bufferFramesToUpload = CopyOnWriteArrayList<LoadedBufferFrameData>(mutableListOf())
    private val framesLoadingCoroutineScope = CoroutineScope(Dispatchers.Default)

    private var needToReinitializeBuffers = false
    private var haveNewFramesSelection = false

    private var isVirtualizationEnabled = false

    fun changeModelsCommonMatrix(newMatrix: Matrix4f) {
        modelsCommonMatrix = newMatrix
    }

    override fun onSceneFramesUpdated() {
        needToReinitializeBuffers = true
    }

    override fun onFramesSelectionUpdated() {
        disableVirtualization()
        haveNewFramesSelection = true
    }

    override fun onGridWidthUpdated() {
        disableVirtualization()
        haveNewFramesSelection = true
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
        shaderProgram.uploadFloat(TexturesRatioUniformName, framesRatio)
        shaderProgram.uploadVector2f(CameraPositionUniformName, getScreenTopLeftGridCellPosition().toFloatVector())
        shaderProgram.uploadFloat(FramesSpacingUniformName, FramesSpacing)
    }

    override fun createNewBatch(zIndex: Int) =
        RenderBatch(maxBatchSize, zIndex, PrimitiveType.Point, listOf(ShaderAttributeType.FLOAT))

    override fun updateBatchesData() {
        selectedFrames.forEachIndexed { index, _ ->
            val batch = getAvailableBatch(null, 0)
            batch.pushInt(index)
        }

        glActiveTexture(GL_TEXTURE0)
        bufferFramesArrayTexture?.bind()
    }

    override fun beforeRender() {
        if (needToReinitializeBuffers) {
            reinitializeBuffers()
        }

        if (haveNewFramesSelection) {
            bufferFramesToUpload.clear()
            uploadAllFramesToBuffer()
            haveNewFramesSelection = false
            enableVirtualization()
        }

        if (!isVirtualizationEnabled) {
            return
        }

        uploadLoadedFramesToBuffers()
        updateBuffersTextures()
    }

    private fun disableVirtualization() {
        isVirtualizationEnabled = false
    }

    private fun enableVirtualization() {
        cameraLastGridCellPosition = getScreenTopLeftGridCellPosition()
        isVirtualizationEnabled = true
    }

    private fun reinitializeBuffers() {
        bufferFramesArrayTexture?.delete()
        initializeTexturesBuffers(frames)
        needToReinitializeBuffers = false
    }

    private fun uploadLoadedFramesToBuffers() {
        val uploadedFramesIndices = mutableSetOf<Int>()
        bufferFramesToUpload.sortedBy { it.time }.forEach { frame ->
            bufferFramesToUpload.remove(frame)
            if (!uploadedFramesIndices.contains(frame.bufferIndex)) {
                bufferFramesArrayTexture?.uploadTexture(frame.textureData, frame.bufferIndex)
                uploadedFramesIndices.add(frame.bufferIndex)
            }
            Texture2D.freeData(frame.textureData)
        }
    }

    // Updates the buffers with actual frames textures.
    private fun updateBuffersTextures() {
        val cameraGridCellPosition = getScreenTopLeftGridCellPosition()
        if (cameraGridCellPosition != cameraLastGridCellPosition) {
            loadNewTexturesToBuffers(cameraGridCellPosition)
        }
        cameraLastGridCellPosition = cameraGridCellPosition
    }

    // Defines textures that became visible in the last draw call and uploads them to the buffers.
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
            rectHeight = min(buffersSize.y, gridHeight)
        } else {
            rectHeight = abs(gridCellPositionDelta.y)
            rectWidth = min(buffersSize.x, gridWidth)
        }
        val newFramesRect = IntRect(
            if (gridCellPositionDelta.x > 0) {
                cameraPosition.x + buffersSize.x - gridCellPositionDelta.x
            } else {
                max(0, cameraPosition.x)
            },
            if (gridCellPositionDelta.y > 0) {
                cameraPosition.y + buffersSize.y - gridCellPositionDelta.y
            } else {
                max(0, cameraPosition.y)
            },
            rectWidth,
            rectHeight
        )
        loadRectFramesToBuffers(newFramesRect)
    }

    private fun getFramesAtRect(rect: IntRect): List<List<VisualizationFrame>> {
        if (selectedFrames.isEmpty()) {
            return emptyList()
        }

        val framesRect = mutableListOf<List<VisualizationFrame>>()

        for (y in rect.y0 until rect.y0 + rect.height) {
            val framesFromIndex = (gridWidth * y + rect.x0).coerceIn(0..selectedFrames.lastIndex)
            val framesToIndex = (framesFromIndex + rect.width).coerceIn(0..selectedFrames.count())

            framesRect.add(selectedFrames.subList(framesFromIndex, framesToIndex))
        }

        return framesRect
    }

    // Uploads the rect of frames (that was taken from frames selection) to the buffers.
    private fun loadRectFramesToBuffers(framesRect: IntRect) {
        val rectFrames = getFramesAtRect(framesRect)

        if (rectFrames.isEmpty() || rectFrames.first().isEmpty()) {
            return
        }

        val buffersOffset = Vector2i(framesRect.x0 % buffersSize.x, framesRect.y0 % buffersSize.y)
        val uploadedBuffersIndices = mutableSetOf<Int>()

        for (y in 0 until rectFrames.count()) {
            for (x in 0 until rectFrames[y].count()) {
                val textureBuffersIndex =
                    ((buffersOffset.y + y) % buffersSize.y) * buffersSize.x + (buffersOffset.x + x) % buffersSize.x
                if (uploadedBuffersIndices.contains(textureBuffersIndex)) {
                    continue
                }

                uploadFrameToBuffersArray(rectFrames[y][x], textureBuffersIndex)
                uploadedBuffersIndices.add(textureBuffersIndex)
            }
        }
    }

    // Returns the integer position of the frame cell located in the top-left corner of the screen.
    private fun getScreenTopLeftGridCellPosition(): Vector2i {
        val cameraGridCellPosition = getCameraCellPosition()
        return (cameraGridCellPosition - Vector2f(buffersSize) / 2f).toIntVector()
    }

    private fun uploadAllFramesToBuffer() {
        loadRectFramesToBuffers(IntRect(0, 0, gridWidth, gridHeight))
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
        buffersSize = Vector2i(
            (window.width / (framesWidth * SceneController.DefaultMinScale)).toInt() + BuffersSizeOffset,
            (window.height / (framesHeight * SceneController.DefaultMinScale)).toInt() + BuffersSizeOffset
        )

        bufferFramesArrayTexture =
            ArrayTexture(framesWidth, framesHeight, framesChannelsType, buffersSize.x * buffersSize.y)
    }

    private fun uploadFrameToBuffersArray(frame: VisualizationFrame, index: Int) {
        val loadTime = Date().time
        framesLoadingCoroutineScope.launch {
            val textureData = Texture2D.loadData(frame.imagePath.toString())
            if (textureData == null) {
                println("The read texture is null!")
                return@launch
            }

            bufferFramesToUpload.add(LoadedBufferFrameData(textureData, index, loadTime))
        }
    }

    companion object {
        private const val ModelUniformName = "uModel"
        private const val BuffersSizeUniformName = "uBuffersSize"
        private const val TexturesArrayUniformName = "uTextures"
        private const val TexturesRatioUniformName = "uTexturesRatio"
        private const val CameraPositionUniformName = "uCameraPosition"

        private const val BuffersSizeOffset = 2

        private val defaultBuffersSize = Vector2i(10, 10)
    }
}
