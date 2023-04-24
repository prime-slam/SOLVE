package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleObjectProperty
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
    colorProperty: SimpleObjectProperty<Color>
) {
    private val line = Line()
    val node: Node = line

    init {
        updateLinePosition()
        line.strokeProperty().bind(colorProperty)

        scale.onChange {
            updateLinePosition()
        }
    }

    fun dispose() {
        line.strokeProperty().unbind()
    }

    private fun updateLinePosition() {
        line.startX = (firstFramePosition.first + firstKeypoint.coordinate.x) * scale.value
        line.startY = (firstFramePosition.second + firstKeypoint.coordinate.y) * scale.value
        line.endX = (secondFramePosition.first + secondKeypoint.coordinate.x) * scale.value
        line.endY = (secondFramePosition.second + secondKeypoint.coordinate.y) * scale.value
    }
}
