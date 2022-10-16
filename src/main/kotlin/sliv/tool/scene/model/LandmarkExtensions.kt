package sliv.tool.scene.model

import javafx.scene.paint.Color

fun Landmark.getHashColor(): Color {
    val rgbRange = Pair(0, 255)
    val red = (this.uid % 2).produceNumberFromRange(rgbRange)
    val green = (this.uid % 3).produceNumberFromRange(rgbRange)
    val blue = (this.uid % 2).produceNumberFromRange(rgbRange)
    return Color.rgb(red, green, blue)
    TODO("Some hash function")
}

private fun Long.produceNumberFromRange(range: Pair<Int, Int>): Int {
    val remainder = (this % (range.second - range.first)).toInt()
    return remainder + range.first
}