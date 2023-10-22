package solve.rendering.engine.rendering.renderers

import org.joml.Matrix4f
import org.joml.Vector2f
import solve.constants.ShadersDefaultFragmentPath
import solve.constants.ShadersDefaultVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.components.SpriteRenderer
import solve.rendering.engine.rendering.batch.PrimitiveType
import solve.rendering.engine.rendering.batch.RenderBatch
import solve.rendering.engine.scene.GameObject
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType

class SpriteRenderer(
    window: Window
) : Renderer(window) {
    override val maxBatchSize = 1000

    private var modelsCommonMatrix: Matrix4f = Matrix4f().identity()
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
        shaderProgram.uploadMatrix4f(projectionUniformName, window.calculateProjectionMatrix())
        shaderProgram.uploadMatrix4f(modelUniformName, modelsCommonMatrix)
    }

    override fun updateBatchesData() {
        spriteRenderers.forEach { spriteRenderer ->
            val sprite = spriteRenderer.sprite ?: return@forEach

            val texture = sprite.texture
            val batch = getAvailableBatch(texture, spriteRenderer.gameObject.transform.zIndex)
            val textureID = batch.getTextureLocalID(texture)
            val scale = spriteRenderer.gameObject.transform.scale
            val position = spriteRenderer.gameObject.transform.position
            val color = spriteRenderer.color
            val uvCoordinates = sprite.uvCoordinates

            spriteLocalVerticesPositions.forEachIndexed { index, vertexPosition ->
                val scaledX = vertexPosition.x * scale.x
                val scaleY = vertexPosition.y * scale.y

                batch.pushVector2f(position)
                batch.pushVector4f(color.toVector4f())
                batch.pushVector2f(uvCoordinates[index])
                batch.pushInt(textureID)
            }
        }
    }

    override fun addGameObject(gameObject: GameObject) {
        val spriteRenderer = gameObject.getComponentOfType<SpriteRenderer>()
        if (spriteRenderer == null) {
            println("The adding gameobject does not has a sprite renderer component!")
            return
        }

        spriteRenderers.add(spriteRenderer)
    }

    override fun removeGameObject(gameObject: GameObject): Boolean {
        val spriteRenderer = gameObject.getComponentOfType<SpriteRenderer>() ?: return false
        return spriteRenderers.remove(spriteRenderer)
    }

    companion object {
        private const val projectionUniformName = "uProjection"
        private const val modelUniformName = "uModel"

        private val spriteLocalVerticesPositions = listOf(
            Vector2f(1f, 1f),
            Vector2f(1f, 0f),
            Vector2f(0f, 0f),
            Vector2f(0f, 1f)
        )
    }
}
