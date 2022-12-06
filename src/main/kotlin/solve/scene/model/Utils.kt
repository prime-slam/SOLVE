package solve.scene.model

import javafx.scene.paint.Color
import kotlin.random.Random

fun generateRandomColorUnique(existingColors: Collection<Color>): Color {
    var color = generateRandomColor()
    while(existingColors.contains(color)) {
        color = generateRandomColor()
    }
    return color
}
fun generateRandomColor(): Color {
    val red = Random.nextInt(0, 256)
    val green = Random.nextInt(0, 256)
    val blue = Random.nextInt(0, 256)
    return Color.rgb(red, green, blue)
}