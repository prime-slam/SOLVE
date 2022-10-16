package sliv.tool.scene.model

import javafx.scene.paint.Color

class LineLayer(name: String) : Layer(name) {
    override fun matchWithColor(landmark: Landmark): Color = landmark.getHashColor()
}