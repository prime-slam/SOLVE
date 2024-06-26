package solve.scene.model

import solve.utils.loadBufferedImage
import tornadofx.*
import solve.utils.structures.Size as DoubleSize

/**
 * Aggregates scene data and manages order of landmarks from different layers on frames.
 *
 * @param frames images with related landmarks selected in the catalog {@link CatalogueController}.
 * @param layerSettings information about layers in the project, used to determine current order of a layer.
 * @param layerStates stores current layers states.
 */
class Scene(
    val frames: List<VisualizationFrame>,
    val layerSettings: List<LayerSettings>,
    val layerStates: List<LayerState>
) : OrderManager<LayerSettings> {
    /**
     * Size of images in the project. Assumes that all images have the same size, but there is no guarantee.
     */
    val frameSize: DoubleSize by lazy {
        frames.firstOrNull()?.let {
            val image = loadBufferedImage(it.imagePath.toString()) ?: return@let null
            return@let DoubleSize(image.width.toDouble(), image.height.toDouble())
        } ?: DoubleSize(0.0, 0.0)
    }

    val layers: List<Layer>
        get() = frames.firstOrNull()?.layers ?: emptyList()

    private val planesLayersStorage = layerSettings.filter {
        it is LayerSettings.PlaneLayerSettings
    }.toMutableList().asReversed()
    private val nonPlanesLayersStorage = layerSettings.filterNot {
        it is LayerSettings.PlaneLayerSettings
    }.toMutableList().asReversed()

    private val changedCallbacks = mutableListOf<() -> Unit>()

    fun getLayersWithCommonSettings(
        layerSettings: LayerSettings,
        framesSelection: List<VisualizationFrame> = this.frames
    ): List<Layer> {
        val layers = mutableListOf<Layer>()
        framesSelection.forEach { frame ->
            val frameLayer = frame.layers.firstOrNull { it.settings == layerSettings } ?: return@forEach
            layers.add(frameLayer)
        }

        return layers
    }

    override fun addOrderChangedListener(action: () -> Unit) {
        changedCallbacks.add(action)
    }

    override fun removeOrderChangedListener(action: () -> Unit) {
        changedCallbacks.remove(action)
    }

    override fun indexOf(element: LayerSettings): Int {
        if (element is LayerSettings.PlaneLayerSettings) {
            return planesLayersStorage.indexOf(element)
        }

        if (nonPlanesLayersStorage.contains(element)) {
            return planesLayersStorage.size + nonPlanesLayersStorage.indexOf(element)
        }

        return -1
    }

    /**
     * Changes index of layer in the storage.
     * If index is invalid IndexOutOfBoundsException is thrown.
     */
    fun changeLayerIndex(element: LayerSettings, index: Int) {
        if (element is LayerSettings.PlaneLayerSettings) {
            planesLayersStorage.move(element, index)
        } else {
            nonPlanesLayersStorage.move(element, index)
        }

        changedCallbacks.forEach { it() }
    }
}
