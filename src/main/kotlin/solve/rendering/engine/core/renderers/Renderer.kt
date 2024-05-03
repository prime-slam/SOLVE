package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.joml.Vector2i
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL11.glDrawElements
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.core.texture.Texture
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.utils.plus
import solve.rendering.engine.utils.toIntVector
import solve.scene.model.VisualizationFrame
import solve.utils.ceilToInt
import kotlin.math.min

abstract class Renderer(protected val window: Window) : Comparable<Renderer> {
    protected abstract val maxBatchSize: Int

    protected var needToRebuffer = true
    protected lateinit var shaderProgram: ShaderProgram
    protected val batches = mutableListOf<RenderBatch>()

    protected var frames = emptyList<VisualizationFrame>()
    protected var selectedFrames = emptyList<VisualizationFrame>()
    protected var framesSize = Vector2f()

    protected var renderPriority = 0

    protected var gridWidth = DefaultGridWidth
    protected var installedGridWidth = gridWidth
    protected val gridHeight: Int
        get() = (frames.count().toFloat() / gridWidth).ceilToInt()

    init {
        initialize()
    }

    open fun onSceneFramesUpdated() { }

    open fun onFramesSelectionUpdated() { }

    open fun onGridWidthUpdated() { }

    fun setNewSceneFrames(frames: List<VisualizationFrame>, framesSize: Vector2f) {
        this.frames = frames
        this.framesSize = framesSize
        onSceneFramesUpdated()
    }

    fun setFramesSelection(selection: List<VisualizationFrame>) {
        selectedFrames = selection
        updateGridWidth()
        onFramesSelectionUpdated()
    }

    fun setNewGridWidth(gridWidth: Int) {
        if (gridWidth < 1) {
            println("The width of the frames grid should be a positive value!")
            return
        }

        installedGridWidth = gridWidth
        updateGridWidth()
        onGridWidthUpdated()
    }

    open fun render() {
        shaderProgram.use()
        beforeRender()

        uploadUniforms(shaderProgram)

        if (needToRebuffer) {
            cleanupBatchesData()
            updateBatchesData()
            rebufferBatches()
        }

        batches.sort()
        batches.sorted().forEach { batch ->
            batch.bind()
            glDrawElements(batch.primitiveType.openGLPrimitive, batch.getVerticesNumber(), GL_UNSIGNED_INT, 0)
            batch.unbind()
        }

        afterRender()
        shaderProgram.detach()
    }

    protected fun updateGridWidth() {
        this.gridWidth = min(installedGridWidth, selectedFrames.count())
    }

    override fun compareTo(other: Renderer): Int {
        return if (renderPriority < other.renderPriority) {
            -1
        } else if (renderPriority > other.renderPriority) {
            1
        } else {
            0
        }
    }

    open fun delete() {
        batches.forEach { it.deleteBuffers() }
        shaderProgram.delete()
    }

    protected fun getAvailableBatch(texture: Texture?, requiredZIndex: Int): RenderBatch {
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

    protected fun getCameraCellPosition(): Vector2f {
        val framesSpacingXFactor = framesSize.x / (getSpacingWidth(framesSize.toIntVector()) + framesSize.x)
        val framesSpacingYFactor = 1f / (1f + FramesSpacing)
        val framesRatio = framesSize.x / framesSize.y

        return Vector2f(
            window.camera.position.x * framesSpacingXFactor / framesRatio,
            window.camera.position.y * framesSpacingYFactor
        )
    }

    private fun initialize() {
        shaderProgram = createShaderProgram()
    }

    private fun cleanupBatchesData() {
        batches.forEach { it.cleanupData() }
    }

    private fun rebufferBatches() {
        batches.forEach { it.rebuffer() }
    }

    protected fun getFrameTopLeftShaderPosition(frameIndex: Int): Vector2f {
        val frameXIndex = frameIndex % gridWidth
        val frameYIndex = frameIndex / gridWidth
        val framesRatio = framesSize.x / framesSize.y

        return Vector2f(
            frameXIndex.toFloat() * framesRatio + frameXIndex * FramesSpacing,
            frameYIndex.toFloat() + frameYIndex * FramesSpacing
        )
    }

    protected fun getFramePixelShaderPosition(frameIndex: Int, framePixelPosition: Vector2f): Vector2f {
        val frameRelativePosition = Vector2f(framePixelPosition) / framesSize.y
        val frameTopLeftPosition = getFrameTopLeftShaderPosition(frameIndex)

        return frameTopLeftPosition + frameRelativePosition
    }

    companion object {
        const val ProjectionUniformName = "uProjection"
        const val GridWidthUniformName = "uGridWidth"
        const val FramesSpacingUniformName = "uFramesSpacing"

        const val DefaultGridWidth = 10
        const val FramesSpacing = 0.02f

        fun getSpacingWidth(framesSize: Vector2i) = framesSize.y * FramesSpacing
    }
}
