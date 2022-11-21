package sliv.tool.scene.view

import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import sliv.tool.scene.model.Landmark

class KeypointView(private val keypoint: Landmark.Keypoint, scale: Double) : LandmarkView(scale) {
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

    private fun createShape(): Ellipse {
        val shape = Ellipse(coordinates.first, coordinates.second, radius, radius)
        shape.fill = keypoint.layer.color
        shape.opacity = keypoint.layer.opacity

        shape.setOnMouseEntered {
            shape.fill = Color.BLUE
        }
        shape.setOnMouseExited {
            shape.fill = keypoint.layer.color
        }

        return shape
    }
}