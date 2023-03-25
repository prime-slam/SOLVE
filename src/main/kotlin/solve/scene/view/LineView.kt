package solve.scene.view

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.createStrokeTransition
import solve.utils.structures.Point as DoublePoint

class LineView(
    private val line: Landmark.Line,
    viewOrder: Int,
    scale: Double,
) : LandmarkView(scale, viewOrder, line) {
    companion object {
        private const val HighlightingScaleFactor: Double = 2.0
    }

    private val width: Double
        get() {
            val scaleFactor = if (scale < 1) scale else 1.0
            val highlightingFactor = if (shouldHighlight) HighlightingScaleFactor else 1.0
            val selectedWidth = line.layerSettings.selectedWidth

            return selectedWidth * scaleFactor * highlightingFactor
        }

    override val node: Line = createShape()

    private val selectedWidthChangedEventHandler = InvalidationListener {
        updateLineWidth(node)
    }
    private val weakSelectedWidthChangedEventHandler = WeakInvalidationListener(selectedWidthChangedEventHandler)

    init {
        setUpShape(node, line.uid)
        addListeners()
    }

    override fun addToFrameDrawer() {}

    private val startCoordinates
        get() = DoublePoint(line.startCoordinate.x.toDouble() * scale, line.startCoordinate.y.toDouble() * scale)

    private val finishCoordinates
        get() = DoublePoint(line.finishCoordinate.x.toDouble() * scale, line.finishCoordinate.y.toDouble() * scale)

    override fun scaleChanged() {
        node.startX = startCoordinates.x
        node.startY = startCoordinates.y
        node.endX = finishCoordinates.x
        node.endY = finishCoordinates.y
        node.strokeWidth = width
    }

    override fun useCommonColorChanged() {
        if (!shouldHighlight) {
            setLineColor(node, line.layerSettings.getColor(line))
        }
    }

    override fun commonColorChanged(newCommonColor: Color) {
        if (line.layerSettings.useCommonColor && !shouldHighlight) {
            setLineColor(node, newCommonColor)
        }
    }

    override fun viewOrderChanged() {}

    override fun highlightShape(duration: Duration) {
        val scaleTransition = Timeline(KeyFrame(duration, KeyValue(node.strokeWidthProperty(), width)))
        val strokeTransition = createStrokeTransition(
            node, line.layerSettings.getUniqueColor(line), duration
        )

        toFront(node)

        scaleTransition.play()
        strokeTransition.play()
    }

    override fun unhighlightShape(duration: Duration) {
        val targetWidth = line.layerSettings.selectedWidth * (if (scale < 1) scale else 1.0)
        val scaleTransition = Timeline(KeyFrame(duration, KeyValue(node.strokeWidthProperty(), targetWidth)))
        val strokeTransition = createStrokeTransition(
            node, line.layerSettings.getColor(line), duration
        )

        scaleTransition.setOnFinished {
            toBack(node)
        }

        scaleTransition.play()
        strokeTransition.play()
    }

    override fun dispose() {
        super.dispose()
        removeListeners()
    }

    private fun addListeners() {
        line.layerSettings.selectedWidthProperty.addListener(weakSelectedWidthChangedEventHandler)
    }

    private fun removeListeners() {
        line.layerSettings.selectedWidthProperty.removeListener(weakSelectedWidthChangedEventHandler)
    }

    private fun createShape(): Line {
        val shape = Line(startCoordinates.x, startCoordinates.y, finishCoordinates.x, finishCoordinates.y)
        setLineColor(shape, line.layerSettings.getColor(line))
        shape.opacity = line.layerSettings.opacity
        shape.strokeWidth = width

        return shape
    }

    private fun setLineColor(shape: Line, newColor: Color) {
        shape.stroke = newColor
    }

    private fun updateLineWidth(shape: Line) {
        shape.strokeWidth = width
    }
}
