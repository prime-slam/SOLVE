package solve.rendering.engine.scene

import solve.rendering.engine.core.renderers.FramesRenderer
import solve.rendering.engine.core.renderers.LandmarkLayerRenderer
import solve.rendering.engine.core.renderers.Renderer

class Scene(val framesRenderer: FramesRenderer) {
    private val _landmarkRenderers = mutableListOf<LandmarkLayerRenderer>()
    val landmarkRenderers: List<LandmarkLayerRenderer>
        get() = _landmarkRenderers

    fun update() {
        render()
    }

    fun addLandmarkRenderer(renderer: LandmarkLayerRenderer) {
        _landmarkRenderers.add(renderer)
    }

    fun clearLandmarkRenderers() {
        landmarkRenderers.forEach { it.delete() }
        _landmarkRenderers.clear()
    }

    private fun render() {
        framesRenderer.render()
        landmarkRenderers.forEach { it.render() }
    }
}
