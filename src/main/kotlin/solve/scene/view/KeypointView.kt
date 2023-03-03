package solve.scene.view

import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.*
import solve.utils.structures.Point
import tornadofx.onChange

class KeypointView(
    private val keypoint: Landmark.Keypoint,
    scale: Double,
) : LandmarkView(scale, keypoint) {
    companion object {
        private const val HighlightingScaleFactor: Double = 2.0
        private val HighlightingAnimationDuration = Duration.millis(500.0)
    }

    private val keypointCoordinates = Point(keypoint.coordinate.x.toDouble(), keypoint.coordinate.y.toDouble())
    private val coordinates: Point
        get() = keypointCoordinates * scale

    override val node: Ellipse = createShape()
    override var lastEnabledColor: Color? = keypoint.layerSettings.color

    init {
        setUpShape(node, keypoint.uid)
    }

    override fun drawOnCanvas(canvas: BufferedImageView) { }

    private val radius: Double
        get() {
            val selectedRadius = keypoint.layerSettings.selectedRadius
            return if (scale < 1) selectedRadius * scale else selectedRadius
        }

    override fun scaleChanged() {
        updateKeypointTransform()
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
        val shape = Ellipse(coordinates.x, coordinates.y, radius, radius)
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

    private fun updateKeypointTransform() {
        node.centerX = coordinates.x
        node.centerY = coordinates.y
        node.radiusX = radius
        node.radiusY = radius
    }
}