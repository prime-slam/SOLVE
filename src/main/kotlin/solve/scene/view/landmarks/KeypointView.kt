package solve.scene.view.landmarks

import javafx.beans.InvalidationListener
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.utils.structures.DoublePoint

/**
 * Represent keypoint landmark on a frame.
 */
class KeypointView(
    private val keypoint: Landmark.Keypoint,
    viewOrder: Int,
    scale: Double
) : LandmarkView(scale, viewOrder, keypoint) {
    companion object {
        private const val HighlightingScaleFactor: Double = 2.0
    }

    private val keypointCoordinates = DoublePoint(keypoint.coordinate.x.toDouble(), keypoint.coordinate.y.toDouble())

    /**
     * Actual visual coordinates.
     */
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

    /**
     * Do nothing because keypoint drawn in visual tree.
     */
    override fun addToFrameDrawer() {}

    /**
     * Don't change radius on zooming in.
     */
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

    /**
     * Do nothing, because node's view order is set in the parent class.
     */
    override fun viewOrderChanged() {}

    /**
     * Plays highlighting animation.
     */
    override fun highlightShape(duration: Duration) {
<<<<<<< HEAD
        val scaleTransition =
            animationProvider.createScaleTransition(node, HighlightingScaleFactor, HighlightingScaleFactor, duration)
        val fillTransition = animationProvider.createFillTransition(
=======
        val scaleTransition = createScaleTransition(node, HighlightingScaleFactor, HighlightingScaleFactor, duration)
        val fillTransition = createFillTransition(
>>>>>>> 13f8702 (Just an automaticly formatted codestyle)
            node,
            keypoint.layerSettings.getUniqueColor(keypoint),
            duration
        )

        toFront(node)

        scaleTransition.play()
        fillTransition.play()
    }

    /**
     * Plays unhighlighting animation.
     */
    override fun unhighlightShape(duration: Duration) {
        val scaleTransition = animationProvider.createScaleTransition(node, 1.0, 1.0, duration)
        val fillTransition =
            animationProvider.createFillTransition(node, keypoint.layerSettings.getColor(keypoint), duration)

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
