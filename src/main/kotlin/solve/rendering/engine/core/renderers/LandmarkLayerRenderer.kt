package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import solve.rendering.engine.Window
import solve.rendering.engine.utils.plus
import solve.scene.model.Layer
import solve.scene.model.Scene
import solve.scene.model.VisualizationFrame

abstract class LandmarkLayerRenderer(
    window: Window,
    protected val getScene: () -> Scene?
) : Renderer(window) {
    protected var framesRatio: Float = 1f

    abstract fun setFramesSelectionLayers(layers: List<Layer>)

    override fun onSceneFramesUpdated() {
        this.framesRatio = framesSize.x / framesSize.y
    }

    protected fun getFrameTopLeftShaderPosition(frameIndex: Int): Vector2f {
        val frameXIndex = frameIndex % gridWidth
        val frameYIndex = frameIndex / gridWidth

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
}
