package solve.scene.view.landmarks

import javafx.beans.InvalidationListener
import javafx.collections.SetChangeListener
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.HIGHLIGHTING_VIEW_ORDER_GAP
import solve.scene.view.LANDMARKS_VIEW_ORDER
import solve.scene.view.drawing.FrameDrawer
import solve.scene.view.drawing.FrameEventManager
import solve.utils.ServiceLocator
import tornadofx.ChangeListener
import tornadofx.visibleWhen

/**
 * Represents landmark on the frame. Can be drawn both using visual tree either using canvas.
 * Has access to landmark data class and its layer to apply common layer properties.
 */
sealed class LandmarkView(
    scale: Double,
    viewOrder: Int,
    private val landmark: Landmark
) {
    companion object {
        fun create(
            landmark: Landmark,
            viewOrder: Int,
            scale: Double,
            frameDrawer: FrameDrawer,
            frameEventManager: FrameEventManager
        ): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(landmark, viewOrder, scale)
                is Landmark.Line -> LineView(landmark, viewOrder, scale)
                is Landmark.Plane -> PlaneView(landmark, frameDrawer, frameEventManager, viewOrder, scale)
            }
        }

        val HighlightingAnimationDuration: Duration = Duration.millis(500.0)
        val InstantAnimationDuration: Duration = Duration.millis(0.1)
    }

    /**
     * When shape is created in an inheritor, setUpShape() should be called to set up common features for all landmarks.
     *
     */
    abstract val node: Node?

    /**
     * Ensures right z-order between landmarks from different layers.
     * Landmark with greater viewOrder will be drawn above.
     * Node.viewOrder is double, and node with less viewOrder will be drawn above
     */
    var viewOrder: Int = viewOrder.also { node?.viewOrder = LANDMARKS_VIEW_ORDER - viewOrder }
        set(value) {
            node?.viewOrder = LANDMARKS_VIEW_ORDER - viewOrder
            field = value
            viewOrderChanged()
        }

    /**
     * Adds landmark as visual element to frame drawer if needed.
     */
    abstract fun addToFrameDrawer()

    /**
     * Global service which creates visual animations.
     * Mocked in tests.
     */
    protected val animationProvider
        get() = ServiceLocator.getService<AnimationProvider>() ?: throw IllegalStateException()

    private val layerState = landmark.layerState
    private val layerSettings = landmark.layerSettings

    protected val isSelected get() = layerState.selectedLandmarksUids.contains(landmark.uid)
    private val isHovered get() = layerState.hoveredLandmarksUids.contains(landmark.uid)
    protected val shouldHighlight get() = isSelected || isHovered

    private var isHighlighted = false

    /**
     * Should be called when scene's scale updated.
     */
    var scale: Double = scale
        set(value) {
            field = value
            scaleChanged()
        }

    /**
     * For cases when newly created landmark is already highlighted.
     * Used to play instant animation to highlight landmark in common way as usual.
     */
    private val parentChangedListener: InvalidationListener = InvalidationListener { newValue ->
        if (newValue != null && shouldHighlight) {
            highlightShapeIfNeeded(InstantAnimationDuration)
        }
        removeParentChangedListener()
    }

    private fun removeParentChangedListener() = node?.parentProperty()?.removeListener(parentChangedListener)

    /**
     * Updates highlighting state when some landmark in the layer was clicked.
     */
    private val selectedLandmarksChangedEventHandler = SetChangeListener<Long> { e ->
        if (e.wasAdded() && e.elementAdded == landmark.uid) {
            highlightShapeIfNeeded(HighlightingAnimationDuration)
        }

        if (e.wasRemoved() && e.elementRemoved == landmark.uid) {
            unhighlightShapeIfNeeded(HighlightingAnimationDuration)
        }
    }

    /**
     * Updates highlighting state when some landmark in the layer was hovered.
     */
    private val hoveredLandmarksChangedEventHandler = SetChangeListener<Long> { e ->
        if (isSelected) {
            return@SetChangeListener
        }

        if (e.wasAdded() && e.elementAdded == landmark.uid) {
            highlightShapeIfNeeded(HighlightingAnimationDuration)
        }

        if (e.wasRemoved() && e.elementRemoved == landmark.uid) {
            unhighlightShapeIfNeeded(HighlightingAnimationDuration)
        }
    }

    private val useCommonColorChangedEventHandler = InvalidationListener { useCommonColorChanged() }

    private val commonColorChangedEventHandler = ChangeListener { _, _, newCommonColor ->
        commonColorChanged(newCommonColor)
    }

    init {
        addListeners()
    }

    open fun dispose() {
        removeListeners()
    }

    /**
     * Set up common shape properties.
     * Can not be called during LandmarkView initialization because shape is created by inheritors.
     * Use this function only to interact with the shape.
     */
    protected fun setUpShape(shape: Shape, uid: Long) {
        initializeCommonSettingsBindings(shape)
        shape.parentProperty().addListener(parentChangedListener)

        shape.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            if (isSelected) {
                layerState.selectedLandmarksUids.remove(uid)
                return@addEventHandler
            }
            layerState.selectedLandmarksUids.add(uid)
        }

        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            layerState.hoveredLandmarksUids.add(uid)
        }

        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            layerState.hoveredLandmarksUids.remove(uid)
        }
    }

    /**
     * Brings visual node to the top on highlighting.
     */
    protected fun toFront(node: Node) {
        node.viewOrder -= HIGHLIGHTING_VIEW_ORDER_GAP
    }

    /**
     * Returns visual node back when it was unhighlighted.
     */
    protected fun toBack(node: Node) {
        node.viewOrder += HIGHLIGHTING_VIEW_ORDER_GAP
    }

    protected abstract fun useCommonColorChanged()

    protected abstract fun commonColorChanged(newCommonColor: Color)

    protected abstract fun viewOrderChanged()

    /**
     * Should be called only from highlightShapeIfNeeded.
     */
    protected abstract fun highlightShape(duration: Duration)

    /**
     * Should be called only from highlightShapeIfNeeded.
     */
    protected abstract fun unhighlightShape(duration: Duration)

    protected abstract fun scaleChanged()

    private fun initializeCommonSettingsBindings(landmarkNode: Node) {
        landmarkNode.visibleWhen(landmark.layerSettings.enabledProperty)
    }

    private fun addListeners() {
        landmark.layerState.selectedLandmarksUids.addListener(selectedLandmarksChangedEventHandler)
        landmark.layerState.hoveredLandmarksUids.addListener(hoveredLandmarksChangedEventHandler)

        landmark.layerSettings.useCommonColorProperty.addListener(useCommonColorChangedEventHandler)
        layerSettings.commonColorProperty.addListener(commonColorChangedEventHandler)
    }

    private fun removeListeners() {
        layerState.selectedLandmarksUids.removeListener(selectedLandmarksChangedEventHandler)
        layerState.hoveredLandmarksUids.removeListener(hoveredLandmarksChangedEventHandler)

        layerSettings.useCommonColorProperty.removeListener(useCommonColorChangedEventHandler)
        layerSettings.commonColorProperty.removeListener(commonColorChangedEventHandler)
    }

    /**
     * Ensures that landmark wouldn't be highlighted twice.
     */
    protected fun highlightShapeIfNeeded(duration: Duration) {
        if (isHighlighted) {
            return
        }
        highlightShape(duration)
        isHighlighted = true
    }

    /**
     * Ensures that landmark wouldn't be unhighlighted twice.
     */
    private fun unhighlightShapeIfNeeded(duration: Duration) {
        if (!isHighlighted) {
            return
        }
        unhighlightShape(duration)
        isHighlighted = false
    }
}
