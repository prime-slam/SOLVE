package solve.scene.model

import solve.utils.structures.Size as DoubleSize

class Scene(
    val frames: List<VisualizationFrame>,
    layerSettings: List<LayerSettings>
) : OrderManager<LayerSettings> {
    val frameSize = frames.firstOrNull()?.let {
        val image = it.getImage()
        DoubleSize(image.width, image.height)
    } ?: DoubleSize(0.0, 0.0)

    val canvasLayersCount = layerSettings.count { it.usesCanvas }

    private val planeLayerSettingsStorage =
        layerSettings.filterIsInstance<LayerSettings.PlaneLayerSettings>().toMutableList()
    private val layerSettingsStorage =
        layerSettings.filterNot { it is LayerSettings.PlaneLayerSettings }.toMutableList()

    private val changedCallbacks = mutableListOf<() -> Unit>()

    override fun addOrderChangedListener(action: () -> Unit) {
        changedCallbacks.add(action)
    }

    override fun removeOrderChangedListener(action: () -> Unit) {
        changedCallbacks.remove(action)
    }

    override fun indexOf(element: LayerSettings): Int = when (element) {
        is LayerSettings.PlaneLayerSettings -> planeLayerSettingsStorage.indexOf(element)
        else -> {
            if (layerSettingsStorage.contains(element)) {
                planeLayerSettingsStorage.size + layerSettingsStorage.indexOf(element)
            } else {
                -1
            }
        }
    }

    fun changeLayerIndex(element: LayerSettings, index: Int) {
        when (element) {
            is LayerSettings.PlaneLayerSettings -> planeLayerSettingsStorage.changeLayerIndex(element, index)
            else -> layerSettingsStorage.changeLayerIndex(element, index)
        }
        changedCallbacks.forEach { it() }
    }

    fun getFramesLayerSettings() = frames.flatMap { it.layers.map { layer -> layer.settings } }.distinct()

    private fun<T> MutableList<T>.changeLayerIndex(element: T, index: Int) {
        remove(element)
        add(index, element)
    }
}
