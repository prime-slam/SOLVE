package solve.rendering.engine.rendering.renderers

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import org.lwjgl.opengl.GL11.GL_RGBA
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

class FramesRenderer(
    window: Window
) : Renderer(window) {
    override val maxBatchSize = 1000
    private var modelsCommonMatrix = Matrix4f().identity()
    private var gridWidth = DefaultGridWidth
    private var buffersSize = defaultBuffersSize

    private var framesNumber = 0
    private var framesWidth = 0
    private var framesHeight = 0
    private var framesChannelsType = TextureChannelsType.RGBA

    fun changeModelsCommonMatrix(newMatrix: Matrix4f) {
        modelsCommonMatrix = newMatrix
    }

    private fun initializeBuffersTextures(frames: List<ProjectFrame>) {
        val firstTextureData = Texture.loadData(frames.first().imagePath.toString())
        if (firstTextureData == null) {
            println("The read texture is null!")
            return
        }

        framesWidth = firstTextureData.width
        framesHeight = firstTextureData.height
        framesChannelsType = firstTextureData.channelsType

        val arrayTextureID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D_ARRAY, arrayTextureID)
        glTextureStorage3D(
            arrayTextureID,
            0,
            framesChannelsType.openGLType,
            framesWidth,
            framesHeight,
            128
        )

        frames.forEachIndexed { index, frame ->
            val textureData = Texture.loadData(frame.imagePath.toString())
            if (textureData == null) {
                println("The read texture is null!")
                return@forEachIndexed
            }

            glTextureSubImage3D(
                arrayTextureID,
                0,
                0,
                0,
                0,
                framesWidth,
                framesHeight,
                1,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                textureData.data
            )
        }
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
    }

    override fun createNewBatch(zIndex: Int) =
        RenderBatch(maxBatchSize, zIndex, PrimitiveType.Point, listOf(ShaderAttributeType.FLOAT2))


    override fun updateBatchesData() {
        // TODO
    }

    companion object {
        private const val ProjectionUniformName = "uProjection"
        private const val ModelUniformName = "uModel"
        private const val GridWidthUniformName = "uGridWidth"
        private const val BuffersSizeUniformName = "uBuffersSize"
        private const val TexturesUniformName = "uTextures"

        private const val DefaultGridWidth = 5

        private val defaultBuffersSize = Vector2i(10, 10)
    }
}