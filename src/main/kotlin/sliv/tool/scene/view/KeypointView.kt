package sliv.tool.scene.view

import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import sliv.tool.scene.model.Landmark
import kotlin.math.abs
import kotlin.math.hypot

class KeypointView(gc: GraphicsContext, private val keypoint: Landmark.Keypoint) : LandmarkView(gc) {
    companion object {
        private const val OrdinaryRadius: Double = 10.0
    }

    private var radius = OrdinaryRadius

    override fun draw() {
        gc.fill = keypoint.layer.color
        gc.globalAlpha = keypoint.layer.opacity
        val x = keypoint.coordinate.x.toDouble()
        val y = keypoint.coordinate.y.toDouble()
        when (state) {
            LandmarkState.Ordinary -> gc.fillOval(x, y, radius, radius)
            LandmarkState.Hovered -> {
                gc.fill = Color.BLUE
                gc.fillOval(x, y, radius, radius)
            }
        }
    }

    override fun updateIsHovered(event: MouseEvent) {
        val xDiff = abs(keypoint.coordinate.x - event.x)
        val yDiff = abs(keypoint.coordinate.y - event.y)

        state = if (hypot(xDiff, yDiff) < radius) {
            LandmarkState.Hovered
        } else {
            LandmarkState.Ordinary
        }
    }
}