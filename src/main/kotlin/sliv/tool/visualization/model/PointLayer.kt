package sliv.tool.visualization.model

import javafx.scene.paint.Color
import tornadofx.c

class PointLayer(name: String) : Layer(name) {
    private val color: Color = c(DEFAULT_COLOR)

    override fun matchWithColor(landmark: Landmark): Color = color
}