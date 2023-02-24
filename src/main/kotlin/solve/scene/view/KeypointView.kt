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
    }

    override val node: Ellipse = createShape()
    override var lastEnabledColor: Color? = keypoint.layerSettings.color

    init {
        setUpShape(node, keypoint.uid)
    }

    override fun drawOnCanvas() {}

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

        initializeSettingsBindings(shape)

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