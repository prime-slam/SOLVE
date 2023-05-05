package solve.scene.model

import solve.utils.structures.Size as DoubleSize

/**
 * Aggregates scene data and manages order of landmarks from different layers on frames.
 *
 * @param frames images with related landmarks selected in the catalog {@link CatalogueController}.
 * @param layerSettings information about layers in the project, used to determine current order of a layer.
 */
class Scene(
    val frames: List<VisualizationFrame>,
    val layerSettings: List<LayerSettings>
) : OrderManager<LayerSettings> {
    /**
     * Size of images in the project. Assumes that all images have the same size, but there is no guarantee.
     */
    val frameSize: DoubleSize by lazy {
        frames.firstOrNull()?.let {
            val image = it.getImage()
            DoubleSize(image.width, image.height)
        } ?: DoubleSize(0.0, 0.0)
    }

    private val canvasLayersStorage =
        layerSettings.filter { it.usesCanvas }.toMutableList()

    /**
     * Layers which use frame drawer and which don't should be stored separately
     * to keep canvas layers always above non-canvas.
     */
    private val nonCanvasLayersStorage =
        layerSettings.filterNot { it.usesCanvas }.toMutableList()
    private val changedCallbacks = mutableListOf<() -> Unit>()

    val canvasLayersCount = canvasLayersStorage.size

    override fun addOrderChangedListener(action: () -> Unit) {
        changedCallbacks.add(action)
    }

    override fun removeOrderChangedListener(action: () -> Unit) {
        changedCallbacks.remove(action)
    }

    override fun indexOf(element: LayerSettings): Int {
        if (element.usesCanvas) {
            return canvasLayersStorage.indexOf(element)
        }

        if (nonCanvasLayersStorage.contains(element)) {
            return canvasLayersStorage.size + nonCanvasLayersStorage.indexOf(element)
        }
        return -1
    }

    /**
     * Changes index of layer in the storage.
     * If index is invalid IndexOutOfBoundsException is thrown.
     */
    fun changeLayerIndex(element: LayerSettings, index: Int) {
        when (element) {
            is LayerSettings.PlaneLayerSettings -> canvasLayersStorage.changeLayerIndex(element, index)
            else -> nonCanvasLayersStorage.changeLayerIndex(element, index)
        }
        changedCallbacks.forEach { it() }
    }

    private fun <T> MutableList<T>.changeLayerIndex(element: T, index: Int) {
        remove(element)
        add(index, element)
    }
}
