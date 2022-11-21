package sliv.tool.scene.view

import javafx.scene.shape.Shape
import sliv.tool.scene.model.Landmark

class LineView(private val line: Landmark.Line, scale: Double) : LandmarkView(scale) {
    companion object {
        private const val OrdinaryWidth: Double = 5.0
    }

    override val shape: Shape
        get() = TODO("Not yet implemented")

    override fun scaleChanged() {
        TODO("Not yet implemented")
    }

    private var width = OrdinaryWidth

//    override fun draw(gc: GraphicsContext, scale: Double) {
//        gc.fill = line.layer.getColor(line)
//        gc.globalAlpha = line.layer.opacity
//        val x1 = line.startCoordinate.x.toDouble() * scale
//        val y1 = line.startCoordinate.y.toDouble() * scale
//        val x2 = line.finishCoordinate.y.toDouble() * scale
//        val y2 = line.finishCoordinate.y.toDouble() * scale
//        gc.lineWidth = width
//        when (state) {
//            LandmarkState.Ordinary -> gc.strokeLine(x1, y1, x2, y2)
//            LandmarkState.Hovered -> TODO("Draw hovered line")
//        }
//    }
//
//    override fun isHovered(x: Double, y: Double, scale: Double): Boolean {
//        TODO("Check if the point within the line")
//    }
}