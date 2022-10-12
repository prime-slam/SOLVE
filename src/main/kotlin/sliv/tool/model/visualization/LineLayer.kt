package sliv.tool.model.visualization

import javafx.scene.paint.Color
import sliv.tool.common.toColor

class LineLayer(name: String) : Layer(name) {
    override fun matchWithColor(landmarkUid: Long): Color = landmarkUid.toColor()
}