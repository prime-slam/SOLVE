package sliv.tool.scene.model

import javafx.scene.paint.Color
import sliv.tool.project.model.LayerKind

//Stores common context for landmarks drawing.
//Layers properties being edited in the settings menu.
//Settings menu appearance depends on type of the corresponding layer.
//Meaningful changes here provokes scene redrawing.
sealed class Layer(val name: String) {
    abstract val kind: LayerKind

    class PointLayer(name: String) : Layer(name) {
        override val kind = LayerKind.KEYPOINT
        var color: Color = DEFAULT_COLOR
    }

    class LineLayer(name: String) : Layer(name) {
        override val kind = LayerKind.LINE

        private val colorMap = HashMap<Long, Color>()
        private val usedColors = HashSet<Color>()

        fun getColor(landmark: Landmark): Color {
            val result = colorMap[landmark.uid]
            if (result == null) {
                val color = generateRandomColorUnique(usedColors)
                usedColors.add(color)
                colorMap[landmark.uid] = color
            }
            return colorMap[landmark.uid]!!
        }
    }

    class PlaneLayer(name: String) : Layer(name) {
        override val kind = LayerKind.PLANE
    }

    var opacity: Double = DEFAULT_OPACITY
        set(value) {
            if (value !in 0.0..100.0)
                throw IllegalArgumentException("Percent value should lie between 0 and 100")
            field = value
        }

    var enabled: Boolean = true
}