package solve.scene.view

import javafx.scene.paint.Color
import javafx.scene.shape.Line
import solve.scene.model.Landmark

class LineView(
    private val line: Landmark.Line,
    scale: Double,
) : LandmarkView(scale, line) {
    companion object {
        private const val OrdinaryWidth: Double = 3.0
    }

    private var width = OrdinaryWidth
    override val node: Line = createShape()
    override var lastEnabledColor: Color? = Color.RED // TODO: add a line drawing implementation.

    init {
        setUpShape(node, line.uid)
    }

    override fun drawOnCanvas(canvas: BufferedImageView) { }

    private val startCoordinates
        get() = Pair(line.startCoordinate.x.toDouble() * scale, line.finishCoordinate.y.toDouble() * scale)

    private val finishCoordinates
        get() = Pair(line.finishCoordinate.x.toDouble() * scale, line.finishCoordinate.y.toDouble() * scale)

    override fun scaleChanged() {
        node.startX = startCoordinates.first
        node.startY = startCoordinates.second
        node.endX = finishCoordinates.first
        node.endY = finishCoordinates.second
    }

    private fun createShape(): Line {
        val shape =
            Line(startCoordinates.first, startCoordinates.second, finishCoordinates.first, finishCoordinates.second)
        shape.stroke = line.layerSettings.color
        shape.opacity = line.layerSettings.opacity
        shape.strokeWidth = width
        return shape
    }

    override fun highlightShape() {
    }

    override fun unhighlightShape() {
    }
}