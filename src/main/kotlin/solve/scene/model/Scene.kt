package solve.scene.model

import java.lang.ref.WeakReference

class Scene(
    val frames: List<VisualizationFrame>,
    val layerSettings: List<LayerSettings>
) : OrderManager<LayerSettings> {
    private val planeLayerSettingsStorage =
        layerSettings.filterIsInstance<LayerSettings.PlaneLayerSettings>().toMutableList()
    private val layerSettingsStorage =
        layerSettings.filterNot { it is LayerSettings.PlaneLayerSettings }.toMutableList()

    private val changedCallbacks = mutableListOf<WeakReference<() -> Unit>>()

    override fun addOrderChangedListener(action: () -> Unit) {
        changedCallbacks.add(WeakReference(action))
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
        changedCallbacks.forEach { ref ->
            val callback = ref.get() ?: return@forEach
            callback()
        }
    }

    private fun<T> MutableList<T>.changeLayerIndex(element: T, index: Int) {
        remove(element)
        add(index, element)
    }
}