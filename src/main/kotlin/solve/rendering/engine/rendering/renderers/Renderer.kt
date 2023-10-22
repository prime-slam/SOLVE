package solve.rendering.engine.rendering.renderers

import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11.glDrawElements
import solve.rendering.engine.Window
import solve.rendering.engine.rendering.batch.RenderBatch
import solve.rendering.engine.rendering.texture.Texture
import solve.rendering.engine.scene.GameObject
import solve.rendering.engine.shader.ShaderProgram

abstract class Renderer(protected val window: Window) {
    protected abstract val maxBatchSize: Int

    protected var needToRebuffer = true
    private lateinit var shaderProgram: ShaderProgram
    private val batches = mutableListOf<RenderBatch>()

    init {
        initialize()
    }

    open fun render() {
        beforeRender()
        shaderProgram.use()
        uploadUniforms(shaderProgram)

        if (needToRebuffer) {
            updateBatchesData()
            rebufferBatches()
        }

        batches.forEach { batch ->
            batch.bind()
            glDrawElements(batch.primitiveType.openGLPrimitive, batch.getVerticesNumber(), GL_UNSIGNED_INT, 0)
            batch.unbind()
        }
        shaderProgram.detach()
    }

    fun cleanup() {
        batches.forEach { it.deleteBuffers() }
    }

    protected fun getAvailableBatch(texture: Texture, requiredZIndex: Int): RenderBatch {
        batches.forEach { batch ->
            if (!batch.isFull && !batch.isTexturesFull && batch.zIndex == requiredZIndex) {
                batch.addTexture(texture)
                return batch
            }

            if (batch.isTexturesFull && batch.containsTexture(texture) && batch.zIndex == requiredZIndex) {
                return batch
            }
        }

        val batch = createNewBatch(requiredZIndex)
        batch.addTexture(texture)
        batches.add(batch)

        return batch
    }

    protected open fun beforeRender() { }

    protected abstract fun createShaderProgram(): ShaderProgram

    protected abstract fun createNewBatch(zIndex: Int): RenderBatch

    protected abstract fun uploadUniforms(shaderProgram: ShaderProgram)

    protected abstract fun updateBatchesData()

    protected abstract fun addGameObject(gameObject: GameObject)

    protected abstract fun removeGameObject(gameObject: GameObject): Boolean

    private fun initialize() {
        shaderProgram = createShaderProgram()
    }

    private fun rebufferBatches() {
        batches.forEach { it.rebuffer() }
    }
}
