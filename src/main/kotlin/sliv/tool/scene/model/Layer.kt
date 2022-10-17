package sliv.tool.scene.model

//Stores common context for landmarks drawing.
//Layers properties being edited in the settings menu.
//Settings menu appearance depends on type of the corresponding layer.
//Meaningful changes here provokes scene redrawing.
sealed class Layer(val name: String) {
    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (value !in 0.0..100.0)
                throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    var enabled: Boolean = true
}