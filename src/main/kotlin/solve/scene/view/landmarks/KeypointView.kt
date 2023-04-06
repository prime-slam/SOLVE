package solve.scene.view.landmarks

import javafx.beans.InvalidationListener
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.createFillTransition
import solve.scene.view.utils.createScaleTransition
import solve.utils.structures.DoublePoint

class KeypointView(
    private val keypoint: Landmark.Keypoint,
    viewOrder: Int,
    scale: Double,
) : LandmarkView(scale, viewOrder, keypoint) {
    companion object {
        private const val HighlightingScaleFactor: Double = 2.0
    }

    private val keypointCoordinates = DoublePoint(keypoint.coordinate.x.toDouble(), keypoint.coordinate.y.toDouble())
    private val coordinates: DoublePoint
        get() = keypointCoordinates * scale

    override val node: Ellipse = createShape()

    private val selectedRadiusChangedEventHandler = InvalidationListener {
        updateShapeRadius(node)
    }

    init {
        setUpShape(node, keypoint.uid)
        addListeners()
    }

    override fun addToFrameDrawer() {}

    private val radius: Double
        get() {
            val selectedRadius = keypoint.layerSettings.selectedRadius
            return if (scale < 1) selectedRadius * scale else selectedRadius
        }

    override fun scaleChanged() {
        updateKeypointTransform()
    }

    override fun useCommonColorChanged() {
        if (!shouldHighlight) {
            setKeypointColor(node, keypoint.layerSettings.getColor(keypoint))
        }
    }

    override fun commonColorChanged(newCommonColor: Color) {
        if (keypoint.layerSettings.useCommonColor && !shouldHighlight) {
            setKeypointColor(node, newCommonColor)
        }
    }

    override fun viewOrderChanged() {}

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
        val fillTransition = createFillTransition(node, keypoint.layerSettings.getColor(keypoint), duration)

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
        val shape = Ellipse(coordinates.x, coordinates.y, radius, radius)
        setKeypointColor(shape, keypoint.layerSettings.getColor(keypoint))
        shape.opacity = keypoint.layerSettings.opacity

        return shape
    }

    private fun addListeners() {
        keypoint.layerSettings.selectedRadiusProperty.addListener(selectedRadiusChangedEventHandler)
    }

    private fun removeListeners() {
        keypoint.layerSettings.selectedRadiusProperty.removeListener(selectedRadiusChangedEventHandler)
    }

    private fun updateShapeRadius(shape: Ellipse) {
        shape.radiusX = radius
        shape.radiusY = radius
    }

    private fun setKeypointColor(shape: Ellipse, newColor: Color) {
        shape.fill = newColor
    }

    private fun updateKeypointTransform() {
        node.centerX = coordinates.x
        node.centerY = coordinates.y
        node.radiusX = radius
        node.radiusY = radius
    }
}
