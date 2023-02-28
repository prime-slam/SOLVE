package solve.scene.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.paint.Color

// Contains settings that should be reused when scene is recreated
// Stores common context for landmarks drawing.
// Layers properties being edited in the settings menu.
// Settings menu appearance depends on type of the corresponding layer.
// Meaningful changes here provokes scene redrawing.
sealed class LayerSettings(val name: String, private val layerColorManager: ColorManager<String>) {
    // Is used to set unique colors for all landmarks in the layer
    private val colorManager = ColorManager<Long>()

    var color = layerColorManager.getColor(name)
        set(value) {
            field = value
            layerColorManager.setColor(name, value)
        }

    fun getColor(landmark: Landmark): Color {
        if (useOneColor.value) {
            return color
        }
        return getUniqueColor(landmark)
    }

    fun getUniqueColor(landmark: Landmark): Color = colorManager.getColor(landmark.uid)

    class PointLayerSettings(name: String, layerColorManager: ColorManager<String>) :
        LayerSettings(name, layerColorManager) {
    }

    class LineLayerSettings(name: String, layerColorManager: ColorManager<String>) :
        LayerSettings(name, layerColorManager) {
    }

    class PlaneLayerSettings(name: String, layerColorManager: ColorManager<String>) :
        LayerSettings(name, layerColorManager) {
        init {
            useOneColor.value = false
        }
    }

    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (value !in 0.0..1.0) throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    var enabled: Boolean = true

    var useOneColor = SimpleBooleanProperty(true)
}