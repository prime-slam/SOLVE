package sliv.tool.scene.view

import javafx.animation.FillTransition
import javafx.animation.ScaleTransition
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.util.Duration
import sliv.tool.scene.model.Landmark

class KeypointView(private val keypoint: Landmark.Keypoint, scale: Double) : LandmarkView(scale, keypoint) {
    companion object {
        private const val OrdinaryRadius: Double = 5.0
    }

    override val shape: Ellipse = createShape()

    private val coordinates
        get() = Pair(keypoint.coordinate.x.toDouble() * scale, keypoint.coordinate.y.toDouble() * scale)

    private val radius
        get() = if (scale < 1) OrdinaryRadius * scale else OrdinaryRadius

    override fun scaleChanged() {
        shape.centerX = coordinates.first
        shape.centerY = coordinates.second
        shape.radiusX = radius
        shape.radiusY = radius
    }

    override fun select() {
        shape.toFront()

        val scaleTransition = ScaleTransition()
        scaleTransition.duration = Duration(500.0)
        scaleTransition.toX = 2.0
        scaleTransition.toY = 2.0
        scaleTransition.node = shape

        val fillTransition = FillTransition()
        fillTransition.duration = Duration(500.0)
        fillTransition.toValue = Color.BLUE
        fillTransition.shape = shape
        scaleTransition.play()
        fillTransition.play()
    }

    override fun unselect() {
        val scaleTransition = ScaleTransition()
        scaleTransition.duration = Duration.millis(500.0)
        scaleTransition.toX = 1.0
        scaleTransition.toY = 1.0
        scaleTransition.node = shape

        val fillTransition = FillTransition()
        fillTransition.duration = Duration(500.0)
        fillTransition.toValue = keypoint.layer.color
        fillTransition.shape = shape

        scaleTransition.play()
        fillTransition.play()
    }

    override fun highlight() {
        shape.fill = Color.BLUE
    }

    override fun unhighlight() {
        shape.fill = keypoint.layer.color
    }

    private fun createShape(): Ellipse {
        val shape = Ellipse(coordinates.first, coordinates.second, radius, radius)
        shape.fill = keypoint.layer.color
        shape.opacity = keypoint.layer.opacity

        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            select()
        }
        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            unselect()
        }

        return shape
    }
}