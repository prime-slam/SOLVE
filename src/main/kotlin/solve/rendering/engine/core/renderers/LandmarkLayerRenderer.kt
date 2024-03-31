package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.joml.Vector2i
import solve.rendering.engine.Window
import solve.rendering.engine.utils.plus
import solve.scene.model.Layer
import solve.scene.model.VisualizationFrame

abstract class LandmarkLayerRenderer(
    window: Window
) : Renderer(window) {
    private var gridWidth = FramesRenderer.DefaultGridWidth
    private var framesSize = Vector2f()
    private var framesRatio: Float = 1f

    abstract fun setFramesSelectionLayers(layers: List<Layer>)

    override fun setNewSceneFrames(frames: List<VisualizationFrame>, framesSize: Vector2f) {
        this.framesSize = framesSize
        this.framesRatio = framesSize.x / framesSize.y
    }

    fun setNewGridWidth(gridWidth: Int) {
        if (gridWidth < 1) {
            println("The width of the frames grid should be a positive value!")
            return
        }

        this.gridWidth = gridWidth
    }

    protected fun framePixelToShaderPosition(frameIndex: Int, framePixelPosition: Vector2f): Vector2f {
        val frameRelativePosition = Vector2f(framePixelPosition) / framesSize.y
        val previousFramesVector = Vector2f(
            (frameIndex % gridWidth).toFloat() * framesRatio,
            (frameIndex / gridWidth).toFloat()
        )

        return previousFramesVector + frameRelativePosition
    }
}
