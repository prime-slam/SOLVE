package solve.scene.model

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

// Contains settings that should be reused when scene is recreated
// Stores common context for landmarks drawing.
// Layers properties being edited in the settings menu.
// Settings menu appearance depends on type of the corresponding layer.
// Meaningful changes here provokes scene redrawing.
sealed class LayerSettings(val name: String) {
    // Is used to set unique colors for all landmarks in the layer
    val colorManager = ColorManager<Long>()
    abstract val landmarksType: LandmarkType

    class PointLayerSettings(name: String, private val layerColorManager: ColorManager<String>) : LayerSettings(name) {
        override val landmarksType = LandmarkType.Keypoint

        val colorProperty = SimpleObjectProperty(layerColorManager.getColor(name))
        var color: Color
            get() = colorProperty.get()
            set(value) {
                colorProperty.set(value)
                layerColorManager.setColor(name, value)
            }
    }

    class LineLayerSettings(name: String, private val layerColorManager: ColorManager<String>) : LayerSettings(name) {
        override val landmarksType = LandmarkType.Line

        val colorProperty = SimpleObjectProperty(layerColorManager.getColor(name))
        var color: Color
            get() = colorProperty.get()
            set(value) {
                colorProperty.set(value)
                layerColorManager.setColor(name, value)
            }
    }

    class PlaneLayerSettings(name: String) : LayerSettings(name) {
        override val landmarksType = LandmarkType.Plane
    }

    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (value !in 0.0..1.0) throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    val enabledProperty = SimpleObjectProperty(true)
    var enabled: Boolean by enabledProperty
}