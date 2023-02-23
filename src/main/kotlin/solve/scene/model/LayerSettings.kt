package solve.scene.model

// Contains settings that should be reused when scene is recreated
// Stores common context for landmarks drawing.
// Layers properties being edited in the settings menu.
// Settings menu appearance depends on type of the corresponding layer.
// Meaningful changes here provokes scene redrawing.
sealed class LayerSettings(val name: String) {
    // Is used to set unique colors for all landmarks in the layer
    val colorManager = ColorManager<Long>()

    class PointLayerSettings(name: String, private val layerColorManager: ColorManager<String>) : LayerSettings(name) {
        var color = layerColorManager.getColor(name)
            set(value) {
                field = value
                layerColorManager.setColor(name, value)
            }
    }

    class LineLayerSettings(name: String) : LayerSettings(name)

    class PlaneLayerSettings(name: String) : LayerSettings(name)

    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (value !in 0.0..1.0) throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    var enabled: Boolean = true
}