package solve.scene.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

// Contains settings that should be reused when scene is recreated
// Stores common context for landmarks drawing.
// Layers properties being edited in the settings menu.
// Settings menu appearance depends on type of the corresponding layer.
// Meaningful changes here provokes scene redrawing.
// layerName is unique only in the project, layerKey is unique between layers from other projects too,
// so it can be used as a key in color manager
sealed class LayerSettings(
    val layerName: String,
    private val layerKey: String,
    private val layerColorManager: ColorManager<String>
) {
    companion object {
        const val MinOpacity = 0.0
        const val MaxOpacity = 1.0
    }

    // Is used to set unique colors for all landmarks in the layer
    private val colorManager = ColorManager<Long>()

    abstract val usesCanvas: Boolean // True for layers, which draws anything with FrameDrawer

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

    fun getUniqueColor(landmark: Landmark): Color = colorManager.getColor(landmark.uid)

    class PointLayerSettings(
        layerName: String,
        layerKey: String,
        layerColorManager: ColorManager<String>
    ) : LayerSettings(layerName, layerKey, layerColorManager) {
        companion object {
            private const val OrdinaryRadius: Double = 5.0

            const val PointSizeSliderMinValue = 1.0
            const val PointSizeSliderMaxValue = 20.0
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

            const val LineWidthSliderMinValue = 1.0
            const val LineWidthSliderMaxValue = 10.0
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
            if (value !in MinOpacity..MaxOpacity) throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    val enabledProperty = SimpleObjectProperty(true)
    var enabled: Boolean
        get() = enabledProperty.get()
        set(value) = enabledProperty.set(value)
}
