package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11.glDrawElements
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.core.texture.Texture2D
import solve.rendering.engine.shader.ShaderProgram
import solve.scene.model.VisualizationFrame

abstract class Renderer(protected val window: Window) {
    protected abstract val maxBatchSize: Int

    protected var needToRebuffer = true
    private lateinit var shaderProgram: ShaderProgram
    private val batches = mutableListOf<RenderBatch>()

    init {
        initialize()
    }

    abstract fun setNewSceneFrames(frames: List<VisualizationFrame>, framesSize: Vector2f)

    open fun render() {
        shaderProgram.use()
        beforeRender()

        uploadUniforms(shaderProgram)

        if (needToRebuffer) {
            cleanupBatchesData()
            updateBatchesData()
            rebufferBatches()
        }

        batches.forEach { batch ->
            batch.bind()
            glDrawElements(batch.primitiveType.openGLPrimitive, batch.getVerticesNumber(), GL_UNSIGNED_INT, 0)
            batch.unbind()
        }

        afterRender()
        shaderProgram.detach()
    }

    fun delete() {
        batches.forEach { it.deleteBuffers() }
        shaderProgram.delete()
    }

    protected fun getAvailableBatch(texture: Texture2D?, requiredZIndex: Int): RenderBatch {
        batches.forEach { batch ->
            if (batch.isFull || batch.zIndex != requiredZIndex) {
                return@forEach
            }

            if (texture == null) {
                return batch
            }

            if (batch.containsTexture(texture)) {
                return batch
            }

            if (!batch.isTexturesFull) {
                batch.addTexture(texture)
                return batch
            }
        }

        val batch = createNewBatch(requiredZIndex)
        texture?.let { batch.addTexture(texture) }
        batches.add(batch)

        return batch
    }

    protected open fun beforeRender() { }

    protected open fun afterRender() { }

    protected abstract fun createShaderProgram(): ShaderProgram

    protected abstract fun createNewBatch(zIndex: Int): RenderBatch

    protected abstract fun uploadUniforms(shaderProgram: ShaderProgram)

    protected abstract fun updateBatchesData()

    private fun initialize() {
        shaderProgram = createShaderProgram()
    }

    private fun cleanupBatchesData() {
        batches.forEach { it.cleanupData() }
    }

    private fun rebufferBatches() {
        batches.forEach { it.rebuffer() }
    }
}
