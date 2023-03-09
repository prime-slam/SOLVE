package solve.scene.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

// Contains settings that should be reused when scene is recreated
// Stores common context for landmarks drawing.
// Layers properties being edited in the settings menu.
// Settings menu appearance depends on type of the corresponding layer.
// Meaningful changes here provokes scene redrawing.
sealed class LayerSettings(val name: String, private val layerColorManager: ColorManager<String>) {
    // Is used to set unique colors for all landmarks in the layer
    val colorManager = ColorManager<Long>()

    val useOneColor = SimpleBooleanProperty(true)

    val commonColorProperty = SimpleObjectProperty(layerColorManager.getColor(name))
    var commonColor: Color
        get() = commonColorProperty.get()
        set(value) {
            commonColorProperty.set(value)
            layerColorManager.setColor(name, value)
        }

    fun getColor(landmark: Landmark): Color {
        if (useOneColor.value) {
            return commonColor
        }
        return getUniqueColor(landmark)
    }

    fun getUniqueColor(landmark: Landmark): Color = colorManager.getColor(landmark.uid)

    class PointLayerSettings(
        name: String,
        layerColorManager: ColorManager<String>
    ) : LayerSettings(name, layerColorManager) {
        companion object {
            private const val OrdinaryRadius: Double = 5.0
        }

        val selectedRadiusProperty = SimpleObjectProperty(OrdinaryRadius)
        var selectedRadius: Double
            get() = selectedRadiusProperty.get()
            set(value) = selectedRadiusProperty.set(value)
    }

    class LineLayerSettings(
        name: String,
        layerColorManager: ColorManager<String>
    ) : LayerSettings(name, layerColorManager) {
        companion object {
            private const val OrdinaryWidth: Double = 3.0
        }

        val selectedWidthProperty = SimpleObjectProperty(OrdinaryWidth)
        var selectedWidth: Double
            get() = selectedWidthProperty.get()
            set(value) = selectedWidthProperty.set(value)
    }

    class PlaneLayerSettings(
        name: String,
        layerColorManager: ColorManager<String>
    ) : LayerSettings(name, layerColorManager)
    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (value !in 0.0..1.0) throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    val enabledProperty = SimpleObjectProperty(true)
    var enabled: Boolean
        get() = enabledProperty.get()
        set(value) = enabledProperty.set(value)
}
