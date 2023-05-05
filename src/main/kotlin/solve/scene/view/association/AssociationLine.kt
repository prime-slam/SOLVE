package solve.scene.view.association

import javafx.beans.InvalidationListener
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import solve.scene.model.Point
import solve.utils.structures.DoublePoint

/**
 * Creates and manages line between two associated keypoints.
 * Manages color and visibility of line to suit the corresponding layer.
 * Manages position of the line to keep end of the line at keypoints.
 *
 * @param firstFramePosition position of the frame of the first keypoint within the grid.
 * @param secondFramePosition position of the frame of the second keypoint within the grid.
 * @param firstKeypointCoordinate coordinate of the first keypoint within its frame.
 * @param secondKeypointCoordinate coordinate of the second keypoint within its frame.
 */
class AssociationLine(
    private val firstFramePosition: DoublePoint,
    private val secondFramePosition: DoublePoint,
    private val firstKeypointCoordinate: Point,
    private val secondKeypointCoordinate: Point,
    private val scale: DoubleProperty,
    colorProperty: ObjectProperty<Color>,
    enabledProperty: BooleanProperty
) {
    private val line = Line()
    val node: Node = line

    private val scaleChangedListener = InvalidationListener {
        updateLinePosition()
    }

    init {
        updateLinePosition()
        line.strokeProperty().bind(colorProperty)
        line.visibleProperty().bind(enabledProperty)
        scale.addListener(scaleChangedListener)
    }

    fun dispose() {
        line.strokeProperty().unbind()
        line.visibleProperty().unbind()
        scale.removeListener(scaleChangedListener)
    }

    private fun updateLinePosition() {
        line.startX = (firstFramePosition.x + firstKeypointCoordinate.x) * scale.value
        line.startY = (firstFramePosition.y + firstKeypointCoordinate.y) * scale.value
        line.endX = (secondFramePosition.x + secondKeypointCoordinate.x) * scale.value
        line.endY = (secondFramePosition.y + secondKeypointCoordinate.y) * scale.value
    }
}
