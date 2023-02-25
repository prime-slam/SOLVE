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
        private const val HighlightingScaleFactor: Double = 2.0
        private val HighlightingAnimationDuration = Duration.millis(500.0)
    }

    override val node: Ellipse = createShape()
    override var lastEnabledColor: Color? = keypoint.layerSettings.color

    init {
        setUpShape(node, keypoint.uid)
    }

    override fun drawOnCanvas(canvas: BufferedImageView) { }

    private val coordinates
        get() = Pair(keypoint.coordinate.x.toDouble() * scale, keypoint.coordinate.y.toDouble() * scale)

    private val radius: Double
        get() {
            val selectedRadius = keypoint.layerSettings.selectedRadius
            return if (scale < 1) selectedRadius * scale else selectedRadius
        }

    override fun scaleChanged() {
        node.centerX = coordinates.first
        node.centerY = coordinates.second
        node.radiusX = radius
        node.radiusY = radius
    }

    override fun highlightShape() {
        if (!keypoint.layerSettings.enabled) {
            return
        }

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
        if (!keypoint.layerSettings.enabled) {
            return
        }

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

        initializeSettingsBindings(shape)

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

    private fun initializeSettingsBindings(shape: Ellipse) {
        keypoint.layerSettings.colorProperty.onChange { newColor ->
            newColor ?: return@onChange
            setShapeColor(shape, newColor)
        }
        keypoint.layerSettings.enabledProperty.onChange { enabled ->
            enabled ?: return@onChange
            setShapeEnabled(shape, enabled)
        }
        keypoint.layerSettings.selectedRadiusProperty.onChange { selectedRadius ->
            selectedRadius ?: return@onChange
            updateShapeRadius(shape)
        }
    }

    private fun updateShapeRadius(shape: Ellipse) {
        shape.radiusX = radius
        shape.radiusY = radius
    }

    private fun highlightShapeInstantly(shape: Ellipse) {
        toFront(shape)
        shape.radiusX = radius * HighlightingScaleFactor
        shape.radiusY = radius * HighlightingScaleFactor
        shape.fill = keypoint.layerSettings.colorManager.getColor(keypoint.uid)
    }
}