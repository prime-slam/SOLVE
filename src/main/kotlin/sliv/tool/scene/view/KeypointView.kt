package sliv.tool.scene.view

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import sliv.tool.scene.model.Landmark
import kotlin.math.abs
import kotlin.math.hypot

class KeypointView(private val keypoint: Landmark.Keypoint) : LandmarkView() {
    companion object {
        private const val OrdinaryRadius: Double = 10.0
    }

    private var radius = OrdinaryRadius

    override fun draw(gc: GraphicsContext, scale: Double) {
        gc.fill = keypoint.layer.color
        gc.globalAlpha = keypoint.layer.opacity

        radius = if (scale < 1) OrdinaryRadius * scale else OrdinaryRadius
        val x = keypoint.coordinate.x.toDouble() * scale - radius / 2
        val y = keypoint.coordinate.y.toDouble() * scale - radius / 2
        when (state) {
            LandmarkState.Ordinary -> gc.fillOval(x, y, radius, radius)
            LandmarkState.Hovered -> {
                gc.fill = Color.BLUE
                gc.fillOval(x, y, radius, radius)
            }
        }
    }

    override fun isHovered(x: Double, y: Double, scale: Double): Boolean {
        val xDiff = abs(keypoint.coordinate.x - x / scale)
        val yDiff = abs(keypoint.coordinate.y - y / scale)
        return hypot(xDiff, yDiff) < radius / scale
    }
}