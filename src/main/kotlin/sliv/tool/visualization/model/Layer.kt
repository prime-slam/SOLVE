package sliv.tool.visualization.model

import javafx.scene.paint.Color

//Stores common context for landmarks drawing.
//Layers properties being edited in the settings menu.
//Settings menu appearance depends on type of the corresponding layer.
//Meaningful changes here provokes scene redrawing.
abstract class Layer(val name: String) {
    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (!(0.0..100.0).contains(value))
                throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    var enabled: Boolean = true

    abstract fun matchWithColor(landmark: Landmark): Color
}