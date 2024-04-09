package solve.rendering.engine.scene

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import solve.rendering.engine.core.renderers.FramesRenderer
import solve.rendering.engine.core.renderers.LandmarkLayerRenderer
import solve.scene.model.Layer
import java.util.concurrent.CopyOnWriteArrayList

class Scene(val framesRenderer: FramesRenderer) {
    private val _landmarkRenderers = CopyOnWriteArrayList<LandmarkLayerRenderer>()
    private val _landmarkRenderersLayers = mutableMapOf<LandmarkLayerRenderer, Layer>()
    val landmarkRenderers: List<LandmarkLayerRenderer>
        get() = _landmarkRenderers
    val landmarkLayerRendererLayers: Map<LandmarkLayerRenderer, Layer>
        get() = _landmarkRenderersLayers

    var isFramesRendererInitializing = false

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

    fun initializeFramesRenderer() {
        CoroutineScope(Dispatchers.Default).launch {
            isFramesRendererInitializing = true
            delay(FrameRendererInitializationDelayMillis)
            isFramesRendererInitializing = false
        }
    }

    private fun render() {
        framesRenderer.render()
        if (isFramesRendererInitializing) {
            return
        }

        _landmarkRenderers.sort()
        _landmarkRenderers.forEach { it.render() }
    }

    companion object {
        private const val FrameRendererInitializationDelayMillis = 1000L
    }
}
