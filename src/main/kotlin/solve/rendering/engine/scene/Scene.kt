package solve.rendering.engine.scene

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.joml.Vector2i
import solve.rendering.engine.core.renderers.FramesRenderer
import solve.rendering.engine.core.renderers.LandmarkLayerRenderer
import solve.rendering.engine.core.renderers.PointAssociationsRenderer
import solve.rendering.engine.utils.toFloatVector
import solve.scene.model.Layer
import solve.scene.model.Scene
import solve.scene.model.VisualizationFrame
import java.util.concurrent.CopyOnWriteArrayList

class Scene(
    val framesRenderer: FramesRenderer,
    val pointAssociationsRenderer: PointAssociationsRenderer,
    val getProjectScene: () -> Scene?
) {
    private val _landmarkRenderers = CopyOnWriteArrayList<LandmarkLayerRenderer>()
    private val _landmarkRenderersLayers = mutableMapOf<LandmarkLayerRenderer, Layer>()

    val landmarkRenderers: List<LandmarkLayerRenderer>
        get() = _landmarkRenderers
    val landmarkLayerRendererLayers: Map<LandmarkLayerRenderer, Layer>
        get() = _landmarkRenderersLayers

    var isFramesRendererInitializing = false

    fun setNewScene(scene: Scene) {
        val framesSize = Vector2i(scene.frameSize.width.toInt(), scene.frameSize.height.toInt())

        framesRenderer.setNewSceneFrames(scene.frames, framesSize.toFloatVector())
        pointAssociationsRenderer.setNewSceneFrames(scene.frames, framesSize.toFloatVector())
        landmarkRenderers.forEach { it.setNewSceneFrames(scene.frames, framesSize.toFloatVector()) }
    }

    fun setFramesSelection(selection: List<VisualizationFrame>) {
        framesRenderer.setFramesSelection(selection)
        pointAssociationsRenderer.setFramesSelection(selection)
        _landmarkRenderers.forEach {
            it.setFramesSelection(selection)
        }
        landmarkRenderers.forEach { renderer ->
            val rendererLayerSettings =
                landmarkLayerRendererLayers[renderer]?.settings ?: return@forEach
            val lastLayersWithCommonSettings =
                getProjectScene()?.getLayersWithCommonSettings(rendererLayerSettings, selection) ?: return@forEach
            renderer.setFramesSelectionLayers(lastLayersWithCommonSettings)
        }
    }

    fun setColumnsNumber(columnsNumber: Int) {
        framesRenderer.setNewGridWidth(columnsNumber)
        pointAssociationsRenderer.setNewGridWidth(columnsNumber)
        landmarkRenderers.forEach { it.setNewGridWidth(columnsNumber) }
    }

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

        pointAssociationsRenderer.render()
        _landmarkRenderers.sort()
        _landmarkRenderers.forEach { it.render() }
    }

    companion object {
        private const val FrameRendererInitializationDelayMillis = 1000L
    }
}
