package solve.rendering.engine.scene

import solve.rendering.engine.core.renderers.FramesRenderer
import solve.rendering.engine.core.renderers.LandmarkLayerRenderer
import solve.scene.model.Layer

class Scene(val framesRenderer: FramesRenderer) {
    private val _landmarkRenderers = mutableListOf<LandmarkLayerRenderer>()
    private val _landmarkRenderersLayers = mutableMapOf<LandmarkLayerRenderer, Layer>()
    val landmarkRenderers: List<LandmarkLayerRenderer>
        get() = _landmarkRenderers
    val landmarkLayerRendererLayers: Map<LandmarkLayerRenderer, Layer>
        get() = _landmarkRenderersLayers

    fun update() {
        render()
    }

    fun addLandmarkRenderer(renderer: LandmarkLayerRenderer, layer: Layer) {
        _landmarkRenderers.add(renderer)
        _landmarkRenderersLayers[renderer] = layer
    }

    fun clearLandmarkRenderers() {
        landmarkRenderers.forEach { it.delete() }
        _landmarkRenderers.clear()
        _landmarkRenderersLayers.clear()
    }

    private fun render() {
        framesRenderer.render()
        _landmarkRenderers.sort()
        //_landmarkRenderers.forEach { it.render() }
    }
}
