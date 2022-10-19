package sliv.tool.scene.model

import javafx.scene.paint.Color
import kotlin.random.Random

fun generateRandomColor(): Color {
    val red = generateRandomIntFrom0To255()
    val green = generateRandomIntFrom0To255()
    val blue = generateRandomIntFrom0To255()
    return Color.rgb(red, green, blue)
}

private fun generateRandomIntFrom0To255() = ((Random.nextInt() % 255) + 255) % 255