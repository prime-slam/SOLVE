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
import solve.rendering.engine.utils.toFloatVector
import solve.rendering.engine.utils.toIntVector
import solve.scene.controller.SceneController
import solve.scene.model.VisualizationFrame
import java.nio.file.Path
import java.util.Date

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
    private var gridWidth = DefaultGridWidth
    private var gridHeight = DefaultGridWidth
    private var buffersSize = defaultBuffersSize

    private var bufferFramesArrayTexture: ArrayTexture? = null

    private var testFrame = VisualizationFrame(0L, Path.of(""), emptyList())
    private lateinit var frameData: Texture2DData
    private var frameWidth = 1
    private var frameHeight = 1
    private var frameChannelsType = TextureChannelsType.RGBA

    private val framesRatio: Float
        get() = frameWidth.toFloat() / frameHeight.toFloat()

    private var cameraLastGridCellPosition = getScreenCenterGridCellPosition()

    private val bufferFramesToUpload = mutableListOf<LoadedBufferFrameData>()

    private var needToReinitializeBuffers = false
    private var haveNewFramesSelection = false

    private var isVirtualizationEnabled = false

    fun changeModelsCommonMatrix(newMatrix: Matrix4f) {
        modelsCommonMatrix = newMatrix
    }

    fun setGridSize(gridWidth: Int, gridHeight: Int) {
        this.gridWidth = gridWidth
        this.gridHeight = gridHeight
    }

    fun setTestFrame(frame: VisualizationFrame) {
        this.testFrame = frame
        needToReinitializeBuffers = true
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
        shaderProgram.uploadVector2f(CameraPositionUniformName, getScreenCenterGridCellPosition().toFloatVector())
    }

    override fun createNewBatch(zIndex: Int) =
        RenderBatch(maxBatchSize, zIndex, PrimitiveType.Point, listOf(ShaderAttributeType.FLOAT))

    override fun updateBatchesData() {
        for (index in 0 until (gridWidth * gridHeight)) {
            val batch = getAvailableBatch(null, 0)
            batch.pushInt(index)
        }
    }

    override fun beforeRender() {
        if (needToReinitializeBuffers) {
            reinitializeBuffers()
        }

        if (haveNewFramesSelection) {
            uploadAllFramesToBuffer()
            bufferFramesToUpload.clear()
            haveNewFramesSelection = false
            enableVirtualization()
        }

        if (!isVirtualizationEnabled) {
            return
        }
    }

    private fun disableVirtualization() {
        isVirtualizationEnabled = false
    }

    private fun enableVirtualization() {
        cameraLastGridCellPosition = getScreenCenterGridCellPosition()
        isVirtualizationEnabled = true
    }

    private fun reinitializeBuffers() {
        bufferFramesArrayTexture?.delete()
        initializeTexturesBuffers(testFrame)
        needToReinitializeBuffers = false
        haveNewFramesSelection = true
    }

    private fun getScreenCenterGridCellPosition(): Vector2i {
        val cameraGridCellPosition = Vector2f(window.camera.position.x / framesRatio, window.camera.position.y)
        return (cameraGridCellPosition - Vector2f(buffersSize) / 2f).toIntVector()
    }

    private fun uploadAllFramesToBuffer() {
        uploadFrameToBuffersArray(0)
    }

    private fun initializeTexturesBuffers(frame: VisualizationFrame) {
        val textureData = Texture2D.loadData(frame.imagePath.toString())
        if (textureData == null) {
            println("The read texture is null!")
            return
        }

        frameWidth = textureData.width
        frameData = textureData
        frameHeight = textureData.height
        frameChannelsType = textureData.channelsType
        buffersSize = Vector2i(
            1, 1
        )

        bufferFramesArrayTexture =
            ArrayTexture(frameWidth, frameHeight, frameChannelsType, 1)
    }

    private fun uploadFrameToBuffersArray(index: Int) {
        bufferFramesArrayTexture?.uploadTexture(frameData, index)
    }

    companion object {
        private const val ProjectionUniformName = "uProjection"
        private const val ModelUniformName = "uModel"
        private const val GridWidthUniformName = "uGridWidth"
        private const val BuffersSizeUniformName = "uBuffersSize"
        private const val TexturesArrayUniformName = "uTextures"
        private const val TexturesRatioUniformName = "uTexturesRatio"
        private const val CameraPositionUniformName = "uCameraPosition"

        private const val DefaultGridWidth = 10
        private const val BuffersSizeOffset = 2

        private val defaultBuffersSize = Vector2i(10, 10)
    }
}
