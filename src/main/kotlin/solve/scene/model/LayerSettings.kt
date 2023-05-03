package solve.scene.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

/**
 * Contains settings that should be shared between layers with the same name and project when scene is recreated.
 * Stores common context for {@link Landmark} drawing.
 * Users edit layers properties in the settings panel {@link VisualizationSettingsLayerCell}.
 * @param layerName is unique only inside its project.
 * @param layerKey is unique in all projects, so it can be used as a key in color manager.
 * @param layerColorManager is used to store layers colors (common for all landmarks in a layer).
 */
sealed class LayerSettings(
    val layerName: String,
    private val layerKey: String,
    private val layerColorManager: ColorManager<String>
) {
    companion object {
        const val MinOpacity = 0.0
        const val MaxOpacity = 1.0
    }

    private val colorManager = ColorManager<Long>()

    /**
     * Is used to distinguish layers, which landmarks use {@link FrameDrawer}.
     */
    abstract val usesCanvas: Boolean

    /**
     * If true, all landmarks of the layer should be painted with commonColor
     */
    val useCommonColorProperty = SimpleBooleanProperty(true)
    var useCommonColor: Boolean
        get() = useCommonColorProperty.value
        set(value) = useCommonColorProperty.set(value)

    val commonColorProperty = SimpleObjectProperty(layerColorManager.getColor(layerKey))
    var commonColor: Color
        get() = layerColorManager.getColor(layerKey)
        set(value) {
            layerColorManager.setColor(layerKey, value)
            commonColorProperty.set(value)
        }

    fun getColor(landmark: Landmark): Color {
        if (useCommonColor) {
            return commonColor
        }
        return getUniqueColor(landmark)
    }

    fun getColorWithOpacity(landmark: Landmark): Color {
        val rgbColor = getColor(landmark)

        return Color(rgbColor.red, rgbColor.green, rgbColor.blue, opacity)
    }

    /**
     * Provides unique color force even if useCommonColor is true, used for associations
     */
    fun getUniqueColor(landmark: Landmark): Color = colorManager.getColor(landmark.uid)

    class PointLayerSettings(
        layerName: String,
        layerKey: String,
        layerColorManager: ColorManager<String>
    ) : LayerSettings(layerName, layerKey, layerColorManager) {
        companion object {
            private const val OrdinaryRadius: Double = 5.0

            const val MinSizeValue = 1.0
            const val MaxSizeValue = 20.0
        }

        override val usesCanvas = false

        val selectedRadiusProperty = SimpleObjectProperty(OrdinaryRadius)
        var selectedRadius: Double
            get() = selectedRadiusProperty.get()
            set(value) = selectedRadiusProperty.set(value)
    }

    class LineLayerSettings(
        layerName: String,
        layerKey: String,
        layerColorManager: ColorManager<String>
    ) : LayerSettings(layerName, layerKey, layerColorManager) {
        companion object {
            private const val OrdinaryWidth: Double = 3.0

            const val MinWidthValue = 1.0
            const val MaxWidthValue = 10.0
        }

        override val usesCanvas = false

        val selectedWidthProperty = SimpleObjectProperty(OrdinaryWidth)
        var selectedWidth: Double
            get() = selectedWidthProperty.get()
            set(value) = selectedWidthProperty.set(value)
    }

    class PlaneLayerSettings(
        layerName: String,
        layerKey: String,
        layerColorManager: ColorManager<String>
    ) : LayerSettings(layerName, layerKey, layerColorManager) {
        override val usesCanvas = true

        init {
            useCommonColor = false
        }
    }

    var opacity: Double = DefaultOpacity
        set(value) {
            if (value !in MinOpacity..MaxOpacity) {
                throw IllegalArgumentException("Percent value should lie between 0 and 100")
            }
            field = value
        }

    val enabledProperty = SimpleBooleanProperty(true)
    var enabled: Boolean
        get() = enabledProperty.get()
        set(value) = enabledProperty.set(value)
}
