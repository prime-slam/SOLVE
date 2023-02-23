package solve.scene.model

import javafx.scene.paint.Color
import kotlin.random.Random

class ColorManager<T> {
    private val colorMap = HashMap<T, Color>()

    fun getColor(id: T) = colorMap[id] ?: generateRandomColorUnique(colorMap.values).also {
        colorMap[id] = it
    }

    fun setColor(id: T, color: Color) {
        colorMap[id] = color
    }

    private fun generateRandomColorUnique(existingColors: Collection<Color>): Color {
        var color = generateRandomColor()
        while(existingColors.contains(color)) {
            color = generateRandomColor()
        }
        return color
    }
    private fun generateRandomColor(): Color {
        val red = Random.nextInt(0, 256)
        val green = Random.nextInt(0, 256)
        val blue = Random.nextInt(0, 256)
        return Color.rgb(red, green, blue)
    }
}