package solve.rendering.engine.rendering.renderers

import org.joml.Matrix4f
import org.joml.Vector2i
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY
import org.lwjgl.opengl.GL45.glTextureStorage3D
import org.lwjgl.opengl.GL45.glTextureSubImage3D
import solve.constants.ShadersFrameFragmentPath
import solve.constants.ShadersFrameGeometryPath
import solve.constants.ShadersFrameVertexPath
import solve.project.model.ProjectFrame
import solve.rendering.engine.Window
import solve.rendering.engine.rendering.batch.PrimitiveType
import solve.rendering.engine.rendering.batch.RenderBatch
import solve.rendering.engine.rendering.texture.Texture
import solve.rendering.engine.rendering.texture.TextureChannelsType
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.structures.IntRect
import solve.rendering.engine.utils.minus
import java.nio.ByteBuffer

class FramesRenderer(
    window: Window
) : Renderer(window) {
    override val maxBatchSize = 1000
    private var modelsCommonMatrix = Matrix4f().identity()
    private var gridWidth = DefaultGridWidth
    private var buffersSize = defaultBuffersSize

    private var buffersTexturesArrayID = 0

    private var frames = emptyList<ProjectFrame>()
    private var framesWidth = 1
    private var framesHeight = 1
    private var framesChannelsType = TextureChannelsType.RGBA
    private var cameraFramesOffset = Vector2i(0)

    private var visibleFramesData = emptyMap<ProjectFrame, ByteBuffer>()

    private var cameraLastGridCellPosition = Vector2i(0)

    val texture = Texture("icons/img.png")

    fun changeModelsCommonMatrix(newMatrix: Matrix4f) {
        modelsCommonMatrix = newMatrix
    }

    fun setSceneFrames(frames: List<ProjectFrame>) {
        if (frames.isEmpty()) {
            return
        }

        this.frames = frames
        initializeTexturesBuffers(frames)

        needToRebuffer = true
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
        shaderProgram.uploadInt(TexturesArrayUniformName, buffersTexturesArrayID)
    }

    override fun createNewBatch(zIndex: Int) =
        RenderBatch(maxBatchSize, zIndex, PrimitiveType.Point, listOf(ShaderAttributeType.FLOAT))

    override fun updateBatchesData() {
        frames.forEachIndexed { index, _ ->
            val batch = getAvailableBatch(null, 0)
            // val frameGridCellPosition = Vector2f((1..5).random().toFloat(), (1..5).random().toFloat())
            batch.pushInt(index % 1000)
        }
    }

    override fun beforeRender() {
        texture.bindToSlot(1)

        val cameraGridCellPosition = getCameraGridCellPosition()
        if (cameraGridCellPosition != cameraLastGridCellPosition) {
            updateBuffersTextures(cameraGridCellPosition)
        }
        cameraLastGridCellPosition = cameraGridCellPosition
    }

    private fun updateBuffersTextures(cameraGridCellPosition: Vector2i) {
        val gridCellPositionDelta = cameraGridCellPosition - cameraLastGridCellPosition
        if (gridCellPositionDelta == Vector2i(0)) {
            return
        }

        val newFramesRect = IntRect(
            if (gridCellPositionDelta.x > 0) {
                cameraGridCellPosition.x + buffersSize.x - gridCellPositionDelta.x
            } else {
                cameraGridCellPosition.x
            },
            if (gridCellPositionDelta.y > 0) {
                cameraGridCellPosition.y + buffersSize.y - gridCellPositionDelta.y
            } else {
                cameraGridCellPosition.y
            },
            gridCellPositionDelta.x,
            gridCellPositionDelta.y
        )
        val rectFramesToLoad = getFramesAtRect(newFramesRect)
        loadRectFramesToBuffers(rectFramesToLoad, newFramesRect)
    }

    private fun getFramesAtRect(rect: IntRect): List<List<ProjectFrame>> {
        val framesRect = mutableListOf<List<ProjectFrame>>()
        for (y in rect.y0 until rect.y0 + rect.height) {
            framesRect.add(frames.subList(rect.x0, rect.x0 + rect.width))
        }

        return framesRect
    }

    private fun loadRectFramesToBuffers(rectFrames: List<List<ProjectFrame>>, framesRect: IntRect) {
        if (rectFrames.isEmpty()) {
            return
        }

        val buffersOffset = Vector2i(framesRect.x0 % buffersSize.x, framesRect.y0 % buffersSize.y)
        if (framesRect.width + buffersOffset.x > buffersSize.x || framesRect.height + buffersOffset.y > buffersSize.y) {
            println("The size of the loading frames is out of buffers bounds!")
            return
        }

        for (y in 0 until framesRect.height) {
            for (x in 0 until framesRect.width) {
                val textureBuffersIndex = (y + buffersOffset.y) * buffersSize.x + x + buffersOffset.x
                uploadFrameToBuffer(rectFrames[y][x], textureBuffersIndex)
            }
        }
    }

    private fun getCameraGridCellPosition(): Vector2i {
        if (frames.isEmpty()) {
            return Vector2i(0)
        }

        val cameraPosition = window.camera.position
        val gridCellXPosition = (cameraPosition.x / framesWidth).toInt()
        val gridCellYPosition = (cameraPosition.y / framesHeight).toInt()

        return Vector2i(gridCellXPosition, gridCellYPosition)
    }

    private fun uploadAllFramesToBuffer(frames: List<ProjectFrame>) {
        frames.forEachIndexed { index, frame ->
            uploadFrameToBuffer(frame, index)
        }
    }

    private fun initializeTexturesBuffers(frames: List<ProjectFrame>) {
        val firstTextureData = Texture.loadData(frames.first().imagePath.toString())
        if (firstTextureData == null) {
            println("The read texture is null!")
            return
        }

        framesWidth = firstTextureData.width
        framesHeight = firstTextureData.height
        framesChannelsType = firstTextureData.channelsType

        buffersTexturesArrayID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D_ARRAY, buffersTexturesArrayID)
        glTextureStorage3D(
            buffersTexturesArrayID,
            0,
            framesChannelsType.openGLType,
            framesWidth,
            framesHeight,
            256
        )
    }

    private fun uploadFrameToBuffer(frame: ProjectFrame, index: Int) {
        val textureData = Texture.loadData(frame.imagePath.toString())
        if (textureData == null) {
            println("The read texture is null!")
            return
        }

        glTextureSubImage3D(
            buffersTexturesArrayID,
            index,
            0,
            0,
            0,
            textureData.width,
            textureData.height,
            1,
            textureData.channelsType.openGLType,
            GL_UNSIGNED_BYTE,
            textureData.data
        )

        Texture.freeData(textureData)
    }

    companion object {
        private const val ProjectionUniformName = "uProjection"
        private const val ModelUniformName = "uModel"
        private const val GridWidthUniformName = "uGridWidth"
        private const val BuffersSizeUniformName = "uBuffersSize"
        private const val TexturesArrayUniformName = "uTextures"

        private const val DefaultGridWidth = 50

        private val defaultBuffersSize = Vector2i(10, 10)
    }
}
