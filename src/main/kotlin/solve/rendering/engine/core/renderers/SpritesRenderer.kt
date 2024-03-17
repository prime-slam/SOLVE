package solve.rendering.engine.core.renderers

import org.joml.Matrix4f
import org.joml.Vector2f
import solve.constants.ShadersDefaultFragmentPath
import solve.constants.ShadersDefaultVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.components.SpriteRenderer
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.utils.plus

class SpritesRenderer(
    window: Window
) : Renderer(window) {
    override val maxBatchSize = 1000

    private var modelsCommonMatrix = Matrix4f().identity()
    private val spriteRenderers = mutableListOf<SpriteRenderer>()

    fun changeModelsCommonMatrix(newMatrix: Matrix4f) {
        modelsCommonMatrix = newMatrix
    }

    override fun createShaderProgram(): ShaderProgram {
        val shaderProgram = ShaderProgram()
        shaderProgram.addShader(ShadersDefaultVertexPath, ShaderType.VERTEX)
        shaderProgram.addShader(ShadersDefaultFragmentPath, ShaderType.FRAGMENT)
        shaderProgram.link()

        return shaderProgram
    }

    override fun createNewBatch(zIndex: Int): RenderBatch {
        val shaderAttributesTypes = listOf(
            ShaderAttributeType.FLOAT2,
            ShaderAttributeType.FLOAT4,
            ShaderAttributeType.FLOAT2,
            ShaderAttributeType.FLOAT
        )

        return RenderBatch(
            maxBatchSize,
            zIndex,
            PrimitiveType.Quad,
            shaderAttributesTypes
        )
    }

    override fun uploadUniforms(shaderProgram: ShaderProgram) {
        shaderProgram.uploadMatrix4f(ProjectionUniformName, window.calculateProjectionMatrix())
        shaderProgram.uploadMatrix4f(ModelUniformName, modelsCommonMatrix)
    }

    override fun updateBatchesData() {
        spriteRenderers.forEach { spriteRenderer ->
            val sprite = spriteRenderer.sprite ?: return@forEach

            val texture = sprite.texture
            val textureSidesRatio = texture.width.toFloat() / texture.height.toFloat()
            val batch = getAvailableBatch(texture, spriteRenderer.transform.zIndex)
            val textureID = batch.getTextureLocalID(texture)
            val scale = spriteRenderer.transform.scale
            val position = spriteRenderer.transform.position
            val color = spriteRenderer.color
            val uvCoordinates = sprite.uvCoordinates

            spriteLocalVerticesPositions.forEachIndexed { index, vertexPosition ->
                val vertexLocalVector = Vector2f(
                    vertexPosition.x * scale.x * textureSidesRatio,
                    vertexPosition.y * scale.y
                )

                batch.pushVector2f(position + vertexLocalVector)
                batch.pushVector4f(color.toVector4f())
                batch.pushVector2f(uvCoordinates[index])
                batch.pushInt(textureID)
            }
        }
    }

    fun addSpriteRenderer(spriteRenderer: SpriteRenderer) {
        spriteRenderers.add(spriteRenderer)
    }

    fun removeSpriteRenderer(spriteRenderer: SpriteRenderer): Boolean {
        return spriteRenderers.remove(spriteRenderer)
    }

    companion object {
        private const val ProjectionUniformName = "uProjection"
        private const val ModelUniformName = "uModel"

        private val spriteLocalVerticesPositions = listOf(
            Vector2f(0.5f, 0.5f),
            Vector2f(0.5f, -0.5f),
            Vector2f(-0.5f, -0.5f),
            Vector2f(-0.5f, 0.5f)
        )
    }
}
