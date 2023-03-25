package solve.scene.view

import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.*

class KeypointView(
    private val keypoint: Landmark.Keypoint,
    scale: Double,
) : LandmarkView(scale, keypoint) {
    companion object {
        private const val HighlightingScaleFactor: Double = 2.0
    }

    override val node: Ellipse = createShape()

    private val selectedRadiusChangedEventHandler = InvalidationListener {
        updateShapeRadius(node)
    }
    private val weakSelectedRadiusChangedEventHandler = WeakInvalidationListener(selectedRadiusChangedEventHandler)

    init {
        setUpShape(node, keypoint.uid)
        addListeners()
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
        onColorChangedFillTransition(keypoint.layerSettings.getUniqueColor(keypoint))
    }

    override fun useCommonColorChanged(newCommonColor: Color) {
        onColorChangedFillTransition(newCommonColor)

        keypoint.layerSettings.commonColor = newCommonColor
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

    override fun dispose() {
        super.dispose()
        removeListeners()
    }

    private fun createShape(): Ellipse {
        val shape = Ellipse(coordinates.first, coordinates.second, radius, radius)
        shape.fill = keypoint.layerSettings.getColor(keypoint)
        shape.opacity = keypoint.layerSettings.opacity

        return shape
    }

    private fun addListeners() {
        keypoint.layerSettings.selectedRadiusProperty.addListener(weakSelectedRadiusChangedEventHandler)
    }

    private fun removeListeners() {
        keypoint.layerSettings.selectedRadiusProperty.removeListener(weakSelectedRadiusChangedEventHandler)
    }

    private fun updateShapeRadius(shape: Ellipse) {
        shape.radiusX = radius
        shape.radiusY = radius
    }

    private fun onColorChangedFillTransition(newColor: Color) {
        setShapeColor(node, keypoint.layerSettings.getColor(keypoint))
        if (shouldHighlight) {
            val fillTransition = createFillTransition(
                node, newColor, InstantAnimationDuration
            )
            fillTransition.play()
        }
    }
}
