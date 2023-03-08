package solve.scene.view

import javafx.scene.shape.Line
import solve.scene.model.Landmark

class LineView(
    private val line: Landmark.Line,
    scale: Double,
) : LandmarkView(scale, line) {
    override val node: Line = createShape()

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
        shape.stroke = line.layerSettings.commonColor
        shape.opacity = line.layerSettings.opacity
        shape.strokeWidth = line.layerSettings.selectedWidth
        return shape
    }

    override fun highlightShape() {
    }

    override fun unhighlightShape() {
    }
}