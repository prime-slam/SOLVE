package solve.scene.view

import javafx.beans.InvalidationListener
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
        private val InstantAnimationDuration = Duration.millis(0.1)
    }

    override val node: Ellipse = createShape()

    private val parentChangedListener: InvalidationListener = InvalidationListener { newValue ->
        if (newValue != null && isHighlighted(keypoint)) {
            highlightShape(InstantAnimationDuration)
        }
        removeParentChangedListener()
    }

    private fun removeParentChangedListener() = node.parentProperty().removeListener(parentChangedListener)

    init {
        setUpShape(node, keypoint.uid)
        node.parentProperty().addListener(parentChangedListener)
    }

    override fun drawOnCanvas() { }

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

    override fun useOneColorChanged() {
        node.fill = keypoint.layerSettings.getColor(keypoint)
        if (isHighlighted(keypoint)) {
            val fillTransition = createFillTransition(
                node, keypoint.layerSettings.getUniqueColor(keypoint), InstantAnimationDuration
            )
            fillTransition.play()
        }
    }

    override fun highlightShape() = highlightShape(HighlightingAnimationDuration)

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

        return shape
    }

    private fun highlightShape(duration: Duration) {
        val scaleTransition = createScaleAnimation(node, HighlightingScaleFactor, duration)
        val fillTransition = createFillTransition(
            node, keypoint.layerSettings.getUniqueColor(keypoint), duration
        )

        toFront(node)

        scaleTransition.play()
        fillTransition.play()
    }
}