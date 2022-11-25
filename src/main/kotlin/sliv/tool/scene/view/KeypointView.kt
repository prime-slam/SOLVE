package sliv.tool.scene.view

import javafx.animation.FillTransition
import javafx.animation.ScaleTransition
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.util.Duration
import sliv.tool.scene.model.Landmark

class KeypointView(
    private val keypoint: Landmark.Keypoint,
    scale: Double,
    frameTimestamp: Long,
    eventManager: FramesEventManager
) : LandmarkView(scale, keypoint, frameTimestamp, eventManager) {
    companion object {
        private const val OrdinaryRadius: Double = 5.0
    }

    override val node: Ellipse = createShape().apply {
        setUpShape(this)
    }

    private val coordinates
        get() = Pair(keypoint.coordinate.x.toDouble() * scale, keypoint.coordinate.y.toDouble() * scale)

    private val radius
        get() = if (scale < 1) OrdinaryRadius * scale else OrdinaryRadius

    override fun scaleChanged() {
        node.centerX = coordinates.first
        node.centerY = coordinates.second
        node.radiusX = radius
        node.radiusY = radius
    }

    override fun highlightShape() {
        node.toFront()

        val scaleTransition = ScaleTransition()
        scaleTransition.duration = Duration(500.0)
        scaleTransition.toX = 2.0
        scaleTransition.toY = 2.0
        scaleTransition.node = node

        val fillTransition = FillTransition()
        fillTransition.duration = Duration(500.0)
        fillTransition.toValue = Color.BLUE
        fillTransition.shape = node
        scaleTransition.play()
        fillTransition.play()
    }

    override fun unhighlightShape() {
        val scaleTransition = ScaleTransition()
        scaleTransition.duration = Duration.millis(500.0)
        scaleTransition.toX = 1.0
        scaleTransition.toY = 1.0
        scaleTransition.node = node

        val fillTransition = FillTransition()
        fillTransition.duration = Duration(500.0)
        fillTransition.toValue = keypoint.layer.color
        fillTransition.shape = node

        scaleTransition.play()
        fillTransition.play()
    }

    private fun createShape(): Ellipse {
        val shape = Ellipse(coordinates.first, coordinates.second, radius, radius)
        shape.fill = keypoint.layer.color
        shape.opacity = keypoint.layer.opacity

        return shape
    }
}