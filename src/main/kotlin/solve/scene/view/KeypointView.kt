package solve.scene.view

import javafx.scene.shape.Ellipse
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.*

class KeypointView(
    private val keypoint: Landmark.Keypoint,
    scale: Double,
) : LandmarkView(scale, keypoint) {
    companion object {
        private const val OrdinaryRadius: Double = 5.0
        private const val HighlightingScaleFactor: Double = 2.0
        private val HighlightingAnimationDuration = Duration.millis(500.0)
    }

    override val node: Ellipse = createShape()

    init {
        setUpShape(node, keypoint.uid)
    }

    override fun drawOnCanvas() {}

    private val coordinates
        get() = Pair(keypoint.coordinate.x.toDouble() * scale, keypoint.coordinate.y.toDouble() * scale)

    private val radius
        get() = if (scale < 1) OrdinaryRadius * scale else OrdinaryRadius

    private val highlighted
        get() = keypoint.layerState.selectedLandmarksUids.contains(keypoint.uid)
                || keypoint.layerState.hoveredLandmarksUids.contains(keypoint.uid)

    override fun scaleChanged() {
        node.centerX = coordinates.first
        node.centerY = coordinates.second
        node.radiusX = radius
        node.radiusY = radius
    }

    override fun useOneColorChanged() {
        node.fill = keypoint.layerSettings.getColor(keypoint)
        if (highlighted) {
            val fillTransition = createFillTransition(
                node, keypoint.layerSettings.getUniqueColor(keypoint), Duration.millis(0.1)
            )
            fillTransition.play()
        }
    }

    override fun highlightShape() {
        val increasedRadiusScale = (radius / node.radiusX) * HighlightingScaleFactor
        val scaleTransition = createScaleAnimation(node, increasedRadiusScale, HighlightingAnimationDuration)
        val fillTransition = createFillTransition(
            node, keypoint.layerSettings.getUniqueColor(keypoint), HighlightingAnimationDuration
        )

        toFront(node)

        scaleTransition.play()
        fillTransition.play()
    }

    override fun unhighlightShape() {
        val defaultRadiusScale = radius / node.radiusX
        val scaleTransition = createScaleAnimation(node, defaultRadiusScale, HighlightingAnimationDuration)
        val fillTransition =
            createFillTransition(node, keypoint.layerSettings.getColor(keypoint), HighlightingAnimationDuration)

        scaleTransition.setOnFinished {
            toBack(node)
        }

        scaleTransition.play()
        fillTransition.play()
    }

    private fun createShape(): Ellipse {
        val shape = Ellipse(coordinates.first, coordinates.second, radius, radius)
        shape.fill = keypoint.layerSettings.getColor(keypoint)
        shape.opacity = keypoint.layerSettings.opacity

        // If landmark is already in the selected state.
        // Animation can not be applied because shape is not in visual tree at the moment.
        if (highlighted) {
            highlightShapeInstantly(shape)
        }

        return shape
    }

    private fun highlightShapeInstantly(shape: Ellipse) {
        toFront(shape)
        shape.radiusX = radius * HighlightingScaleFactor
        shape.radiusY = radius * HighlightingScaleFactor
        shape.fill = keypoint.layerSettings.getUniqueColor(keypoint)
    }
}