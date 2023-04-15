package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import solve.scene.model.Landmark
import tornadofx.*

class AssociationLine(
    private val firstFramePosition: Pair<Double, Double>,
    private val secondFramePosition: Pair<Double, Double>,
    private val firstKeypoint: Landmark.Keypoint,
    private val secondKeypoint: Landmark.Keypoint,
    private val scale: DoubleProperty,
    color: Color
) {
    private val line = Line()
    val node: Node = line

    init {
        updateLinePosition()
        line.stroke = color

        scale.onChange {
            updateLinePosition()
        }
    }

    private fun updateLinePosition() {
        line.startX = (firstFramePosition.first + firstKeypoint.coordinate.x) * scale.value
        line.startY = (firstFramePosition.second + firstKeypoint.coordinate.y) * scale.value
        line.endX = (secondFramePosition.first + secondKeypoint.coordinate.x) * scale.value
        line.endY = (secondFramePosition.second + secondKeypoint.coordinate.y) * scale.value
    }
}
