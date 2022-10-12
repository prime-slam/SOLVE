package sliv.tool.model.visualization

import javafx.scene.paint.Color
import sliv.tool.common.*

//Stores common context for landmarks drawing.
//Layers properties being edited in the settings menu.
//Settings menu appearance depends on type of the corresponding layer.
//Meaningful changes here provokes scene redrawing.
abstract class Layer(val name: String) {
    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if(value < 0 || value > 100)
                throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    var enabled: Boolean = true

    abstract fun matchWithColor(landmarkUid: Long) : Color
}