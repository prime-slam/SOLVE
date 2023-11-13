package solve.rendering.engine.rendering.renderers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.joml.Matrix4f
import org.joml.Vector2i
import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL12.glTexImage3D
import org.lwjgl.opengl.GL12.glTexSubImage3D
import org.lwjgl.opengl.GL30.GL_TEXTURE0
import org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY
import org.lwjgl.opengl.GL30.glActiveTexture
import org.lwjgl.opengl.GL30.glGenerateMipmap
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
import solve.rendering.engine.utils.toIntVector
import solve.utils.ceilToInt
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.max

class FramesRenderer(
    window: Window
) : Renderer(window) {
    override val maxBatchSize = 1000
    private var modelsCommonMatrix = Matrix4f().identity()
    private var gridWidth = DefaultGridWidth
    private val gridHeight: Int
        get() = (frames.count().toFloat() / gridWidth).ceilToInt()
    private var buffersSize = defaultBuffersSize

    private var buffersTexturesArrayID = 0

    private var frames = emptyList<ProjectFrame>()
    private var framesWidth = 1
    private var framesHeight = 1
    private var framesChannelsType = TextureChannelsType.RGBA
    private var cameraFramesOffset = Vector2i(0)

    private var visibleFramesData = emptyMap<ProjectFrame, ByteBuffer>()

    private var cameraLastGridCellPosition = Vector2i(0)

    private var textures = mutableListOf<Texture>()
    private var frameBufferIDs = mutableListOf<Int>()

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
        val cameraGridCellPosition = getCameraGridCellPosition()
        if (cameraGridCellPosition != cameraLastGridCellPosition) {
            updateBuffersTextures(cameraGridCellPosition)
        }
        cameraLastGridCellPosition = cameraGridCellPosition
    }

    private fun updateBuffersTextures(cameraGridCellPosition: Vector2i) {
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
        println(newFramesRect)
        loadRectFramesToBuffers(newFramesRect)
    }

    private fun getFramesAtRect(rect: IntRect): List<List<ProjectFrame>> {
        val framesRect = mutableListOf<List<ProjectFrame>>()
        for (y in rect.y0 until rect.y0 + rect.height) {
            val framesFromIndex = (gridWidth * y + rect.x0).coerceIn(0..frames.lastIndex)
            val framesToIndex = (framesFromIndex + rect.width).coerceIn(0..frames.lastIndex)

            framesRect.add(frames.subList(framesFromIndex, framesToIndex))
        }

        return framesRect
    }

    private fun loadRectFramesToBuffers(framesRect: IntRect) {
        val rectFrames = getFramesAtRect(framesRect)
        println(rectFrames)

        if (rectFrames.isEmpty() || rectFrames.first().isEmpty()) {
            return
        }

        val buffersOffset = Vector2i(framesRect.x0 % buffersSize.x, framesRect.y0 % buffersSize.y)
        if (framesRect.width > buffersSize.x || framesRect.height > buffersSize.y) {
            println("The size of the loading frames is out of buffers bounds!")
            return
        }

        for (y in 0 until framesRect.height) {
            for (x in 0 until framesRect.width) {
                val textureBuffersIndex = ((buffersOffset.y + y) % buffersSize.y) * buffersSize.x + (buffersOffset.x + x) % buffersSize.x
                uploadFrameToBuffer(rectFrames[y][x], textureBuffersIndex)
            }
        }
    }

    private fun getCameraGridCellPosition(): Vector2i {
        return window.camera.position.toIntVector()
    }

    private fun uploadAllFramesToBuffer(frames: List<ProjectFrame>) {
        frames.forEachIndexed { index, frame ->
            uploadFrameToBuffer(frame, index)
        }
    }

    private fun uploadInitialFramesToBuffer() {
        loadRectFramesToBuffers(IntRect(0, 0, buffersSize.x, buffersSize.y))
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
        glTexImage3D(
            GL_TEXTURE_2D_ARRAY,
            0,
            framesChannelsType.openGLType,
            framesWidth,
            framesHeight,
            100,
            0,
            framesChannelsType.openGLType,
            GL_UNSIGNED_BYTE,
            null as ByteBuffer?
        )

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glGenerateMipmap(GL_TEXTURE_2D_ARRAY)

        /*for (i in 0 until buffersSize.x * buffersSize.y) {
            val frameBufferID = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID)
            glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, buffersTexturesArrayID, 0, i)
            frameBufferIDs.add(frameBufferID)
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0)*/

        uploadInitialFramesToBuffer()
    }

    private fun uploadFrameToBuffer(frame: ProjectFrame, index: Int) {
        val textureData = Texture.loadData(frame.imagePath.toString())
        if (textureData == null) {
            println("The read texture is null!")
            return
        }

        glTexSubImage3D(
            GL_TEXTURE_2D_ARRAY,
            0,
            0,
            0,
            index,
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

        private const val DefaultGridWidth = 10

        private val defaultBuffersSize = Vector2i(3, 3)
    }
}
