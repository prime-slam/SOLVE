package sliv.tool.common

import javafx.scene.paint.Color

fun Long.toColor() : Color {
    val rgbRange = Pair(0, 255)
    val red = (this % 2).toIntFromRange(rgbRange)
    val green = (this % 3).toIntFromRange(rgbRange)
    val blue = (this % 2).toIntFromRange(rgbRange)
    return Color.rgb(red, green, blue)
    TODO("Some hash function")
}

private fun Long.toIntFromRange(range: Pair<Int, Int>) : Int {
    return (this % (range.second - range.first)).toInt() + range.first
}