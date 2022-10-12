package sliv.tool.model.visualization

import javafx.scene.paint.Color
import sliv.tool.common.*

class PointLayer(name: String) : Layer(name) {
    val color: Color = Color.rgb(DEFAULT_COLOR_RED, DEFAULT_COLOR_GREEN, DEFAULT_COLOR_BLUE)

    override fun matchWithColor(landmarkUid: Long): Color = color
}