package solve.scene.view

import javafx.scene.shape.Ellipse
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.*
import tornadofx.onChange

class KeypointView(
    private val keypoint: Landmark.Keypoint,
    scale: Double,
) : LandmarkView(scale, keypoint) {
    companion object {
        private const val OrdinaryRadius: Double = 5.0
        private const val HighlightingScaleFactor: Double = 2.0
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

    override fun highlightShape(duration: Duration) {
        val scaleTransition = createScaleTransition(node, HighlightingScaleFactor, HighlightingScaleFactor, duration)
        val fillTransition = createFillTransition(
            node, keypoint.layerSettings.getUniqueColor(keypoint), duration
        )

        toFront(node)

        scaleTransition.play()
        fillTransition.play()
    }

    override fun unhighlightShape(duration: Duration) {
        val scaleTransition = createScaleTransition(node, 1.0, 1.0, duration)
        val fillTransition =
            createFillTransition(node, keypoint.layerSettings.getColor(keypoint), duration)

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

        keypoint.layerSettings.colorProperty.onChange {
            shape.fill = keypoint.layerSettings.color
        }

        return shape
    }
}