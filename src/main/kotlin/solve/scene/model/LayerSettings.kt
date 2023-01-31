package solve.scene.model

import javafx.scene.paint.Color
import tornadofx.toObservable

class LayerState(val name: String) {
    val selectedLandmarksUids = mutableSetOf<Long>().toObservable()
    val hoveredLandmarksUids = mutableSetOf<Long>().toObservable()
}

//Stores common context for landmarks drawing.
//Layers properties being edited in the settings menu.
//Settings menu appearance depends on type of the corresponding layer.
//Meaningful changes here provokes scene redrawing.
sealed class LayerSettings(val name: String) {
    private val colorMap = HashMap<Long, Color>()
    private val usedColors = HashSet<Color>()

    fun getColor(landmark: Landmark) = colorMap[landmark.uid] ?: generateRandomColorUnique(usedColors).also { color ->
        colorMap[landmark.uid] = color
        usedColors.add(color)
    }

    class PointLayerSettings(name: String) : LayerSettings(name) {
        var color: Color = DEFAULT_COLOR
    }

    class LineLayerSettings(name: String) : LayerSettings(name)

    class PlaneLayerSettings(name: String) : LayerSettings(name)

    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (value !in 0.0..100.0)
                throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    var enabled: Boolean = true
}