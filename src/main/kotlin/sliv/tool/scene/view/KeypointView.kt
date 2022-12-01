package sliv.tool.scene.view

import javafx.animation.FillTransition
import javafx.animation.ScaleTransition
import javafx.scene.shape.Ellipse
import javafx.util.Duration
import sliv.tool.scene.model.Landmark

class KeypointView(
    private val keypoint: Landmark.Keypoint,
    scale: Double,
) : LandmarkView(scale, keypoint) {
    companion object {
        private const val OrdinaryRadius: Double = 5.0
    }

    override val node: Ellipse = createShape()

    init {
        setUpShape(node)
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

        val increasedRadiusScale = (radius / node.radiusX) * 2.0
        val scaleTransition = ScaleTransition()
        scaleTransition.duration = Duration(500.0)
        scaleTransition.toX = increasedRadiusScale
        scaleTransition.toY = increasedRadiusScale
        scaleTransition.node = node

        val fillTransition = FillTransition()
        fillTransition.duration = Duration(500.0)
        fillTransition.toValue = landmark.layer.getColor(landmark)
        fillTransition.shape = node
        scaleTransition.play()
        fillTransition.play()
    }

    override fun unhighlightShape() {
        val defaultRadiusScale = radius / node.radiusX
        val scaleTransition = ScaleTransition()
        scaleTransition.duration = Duration.millis(500.0)
        scaleTransition.toX = defaultRadiusScale
        scaleTransition.toY = defaultRadiusScale
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

        // If landmark is already in the selected state.
        // Animation can not be applied because shape is not in visual tree at the moment.
        if (landmark.layer.selectedLandmarksUids.contains(landmark.uid)
            || landmark.layer.hoveredLandmarksUids.contains(landmark.uid)
        ) {
            highlightShapeInstantly(shape)
        }

        return shape
    }

    private fun highlightShapeInstantly(shape: Ellipse) {
        shape.radiusX = radius * 2
        shape.radiusY = radius * 2
        shape.fill = landmark.layer.getColor(landmark)
    }
}