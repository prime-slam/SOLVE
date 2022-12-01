package sliv.tool.scene.view

import javafx.scene.shape.Line
import sliv.tool.scene.model.Landmark

class LineView(
    private val line: Landmark.Line,
    scale: Double,
) : LandmarkView(scale, line) {
    companion object {
        private const val OrdinaryWidth: Double = 5.0
    }

    override val node: Line = createShape()

    init {
        setUpShape(node)
    }

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

    private var width = OrdinaryWidth

    private fun createShape(): Line {
        val shape =
            Line(startCoordinates.first, startCoordinates.second, finishCoordinates.first, finishCoordinates.second)
        shape.fill = line.layer.getColor(line)
        shape.opacity = line.layer.opacity
        shape.strokeWidth = width
        return shape
    }

    override fun highlightShape() {
        TODO("Not yet implemented")
    }

    override fun unhighlightShape() {
        TODO("Not yet implemented")
    }
}