package solve.scene.view

import javafx.scene.paint.Color
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
        private val HighlightingAnimationDuration = Duration.millis(500.0)
    }

    override val node: Ellipse = createShape()

    private var lastEnabledColor: Color? = keypoint.layerSettings.color

    init {
        setUpShape(node, keypoint.uid)
    }

    override fun drawOnCanvas(canvas: BufferedImageView) { }

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
        toFront(node)
        val increasedRadiusScale = (radius / node.radiusX) * HighlightingScaleFactor
        val scaleTransition = createScaleAnimation(node, increasedRadiusScale, HighlightingAnimationDuration)
        val fillTransition = createFillTransition(
            node, keypoint.layerSettings.colorManager.getColor(keypoint.uid), HighlightingAnimationDuration
        )

        scaleTransition.play()
        fillTransition.play()
    }

    override fun unhighlightShape() {
        toBack(node)
        val defaultRadiusScale = radius / node.radiusX
        val scaleTransition = createScaleAnimation(node, defaultRadiusScale, HighlightingAnimationDuration)
        val fillTransition = createFillTransition(node, keypoint.layerSettings.color, HighlightingAnimationDuration)

        scaleTransition.play()
        fillTransition.play()
    }

    private fun createShape(): Ellipse {
        val shape = Ellipse(coordinates.first, coordinates.second, radius, radius)
        shape.fill = keypoint.layerSettings.color
        shape.opacity = keypoint.layerSettings.opacity

        keypoint.layerSettings.colorProperty.onChange { newColor ->
            shape.fill = newColor
            lastEnabledColor = newColor
        }
        keypoint.layerSettings.enabledProperty.onChange { enabled ->
            enabled ?: return@onChange
            if (enabled) {
                shape.fill = lastEnabledColor
            } else {
                shape.fill = Color.TRANSPARENT
            }
        }

        // If landmark is already in the selected state.
        // Animation can not be applied because shape is not in visual tree at the moment.
        if (keypoint.layerState.selectedLandmarksUids.contains(keypoint.uid) || keypoint.layerState.hoveredLandmarksUids.contains(
                keypoint.uid
            )
        ) {
            highlightShapeInstantly(shape)
        }

        return shape
    }

    private fun highlightShapeInstantly(shape: Ellipse) {
        toFront(shape)
        shape.radiusX = radius * HighlightingScaleFactor
        shape.radiusY = radius * HighlightingScaleFactor
        shape.fill = keypoint.layerSettings.colorManager.getColor(keypoint.uid)
    }
}