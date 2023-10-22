package solve.rendering.engine.rendering.renderers

import org.joml.Matrix4f
import solve.constants.ShadersDefaultFragmentPath
import solve.constants.ShadersDefaultVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.components.SpriteRenderer
import solve.rendering.engine.rendering.batch.PrimitiveType
import solve.rendering.engine.rendering.batch.RenderBatch
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.scene.GameObject

class SpriteRenderer(window: Window) : Renderer(window) {
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
            MaxBatchSize,
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
        private const val MaxBatchSize = 1000

        private const val projectionUniformName = "uProjection"
        private const val modelUniformName = "uModel"
    }
}