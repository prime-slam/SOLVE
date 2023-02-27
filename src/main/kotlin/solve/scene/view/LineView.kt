package solve.scene.view

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.*

class LineView(
    private val line: Landmark.Line,
    scale: Double,
) : LandmarkView(scale, line) {
    companion object {
        private const val OrdinaryWidth: Double = 3.0
        private const val HighlightingScaleFactor: Double = 2.0
    }

    private val width
        get() = if (scale < 1) OrdinaryWidth * scale else OrdinaryWidth

    override val node: Line = createShape()
    override var lastEnabledColor: Color? = Color.RED // TODO: add a line drawing implementation.

    init {
        setUpShape(node, line.uid)
    }

    override fun drawOnCanvas() {}

    private val startCoordinates
        get() = Pair(line.startCoordinate.x.toDouble() * scale, line.finishCoordinate.y.toDouble() * scale)

    private val finishCoordinates
        get() = Pair(line.finishCoordinate.x.toDouble() * scale, line.finishCoordinate.y.toDouble() * scale)

    override fun scaleChanged() {
        node.startX = startCoordinates.first
        node.startY = startCoordinates.second
        node.endX = finishCoordinates.first
        node.endY = finishCoordinates.second
        node.strokeWidth = width
    }

    override fun useOneColorChanged() {
        node.fill = line.layerSettings.getColor(line)
    }

    override fun highlightShape(duration: Duration) {
        val scaleTransition = createScaleTransition(node, 1.0, HighlightingScaleFactor, duration)
        val strokeTransition = createStrokeTransition(
            node, line.layerSettings.getUniqueColor(line), duration
        )

        toFront(node)

        scaleTransition.play()
        strokeTransition.play()
    }

    override fun unhighlightShape(duration: Duration) {
        val scaleTransition = createScaleTransition(node, 1.0, 1.0, duration)
        val strokeTransition = createStrokeTransition(
            node, line.layerSettings.getColor(line), duration
        )

        scaleTransition.setOnFinished {
            toBack(node)
        }

        scaleTransition.play()
        strokeTransition.play()
    }

    private fun createShape(): Line {
        val shape =
            Line(startCoordinates.first, startCoordinates.second, finishCoordinates.first, finishCoordinates.second)
        shape.stroke = line.layerSettings.getColor(line)
        shape.opacity = line.layerSettings.opacity
        shape.strokeWidth = width
        return shape
    }
}