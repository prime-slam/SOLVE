package sliv.tool.scene.view

import javafx.scene.canvas.GraphicsContext
import sliv.tool.scene.model.Landmark

class LineView(private val line: Landmark.Line) : LandmarkView() {
    companion object {
        private const val OrdinaryWidth: Double = 5.0
    }

    private var width = OrdinaryWidth

    override fun draw(gc: GraphicsContext) {
        gc.fill = line.layer.getColor(line)
        gc.globalAlpha = line.layer.opacity
        val x1 = line.startCoordinate.x.toDouble()
        val y1 = line.startCoordinate.y.toDouble()
        val x2 = line.finishCoordinate.y.toDouble()
        val y2 = line.finishCoordinate.y.toDouble()
        gc.lineWidth = width
        when (state) {
            LandmarkState.Ordinary -> gc.strokeLine(x1, y1, x2, y2)
            LandmarkState.Hovered -> TODO("Draw hovered line")
        }
    }

    override fun isHovered(x: Double, y: Double): Boolean {
        TODO("Check if the point within the line")
    }
}