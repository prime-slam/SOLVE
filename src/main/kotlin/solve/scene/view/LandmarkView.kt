package solve.scene.view

import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.value.WeakChangeListener
import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.util.Duration
import solve.scene.model.Landmark
import tornadofx.ChangeListener
import tornadofx.visibleWhen

// Responsive for creating and setting visual effects for landmarks presenting controls
// This has access to landmark data class and its layer
sealed class LandmarkView(
    scale: Double,
    private val landmark: Landmark,
) {
    companion object {
        fun create(landmark: Landmark, scale: Double, canvas: BufferedImageView): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(landmark, scale)
                is Landmark.Line -> LineView(landmark, scale)
                is Landmark.Plane -> PlaneView(landmark, canvas, scale)
            }
        }

        val HighlightingAnimationDuration: Duration = Duration.millis(500.0)
        val InstantAnimationDuration: Duration = Duration.millis(0.1)
    }

    // When shape is created in an inheritor
    // setUpShape() should be called to set up common features for all landmarks
    abstract val node: Node?

    abstract fun drawOnCanvas()

    private val layerState = landmark.layerState
    private val layerSettings = landmark.layerSettings

    private var state: LandmarkState = LandmarkState.Ordinary

    var scale: Double = scale
        set(value) {
            field = value
            scaleChanged()
        }

    private val parentChangedListener: InvalidationListener = InvalidationListener { newValue ->
        if (newValue != null && isHighlighted(landmark)) {
            highlightShape(InstantAnimationDuration)
        }
        removeParentChangedListener()
    }

    private fun removeParentChangedListener() = node?.parentProperty()?.removeListener(parentChangedListener)

    // Should be stored to avoid weak listener from be collected
    private val selectedLandmarksChangedEventHandler = SetChangeListener<Long> { e ->
        if (e.wasAdded() && e.elementAdded == landmark.uid) {
            state = LandmarkState.Selected
            highlightShape(HighlightingAnimationDuration)
        }

        if (e.wasRemoved() && e.elementRemoved == landmark.uid) {
            state = LandmarkState.Ordinary
            unhighlightShape(HighlightingAnimationDuration)
        }
    }
    private val weakSelectedLandmarksChangedEventHandler = WeakSetChangeListener(selectedLandmarksChangedEventHandler)

    // Should be stored to avoid weak listener from be collected
    private val hoveredLandmarksChangedEventHandler = SetChangeListener<Long> { e ->
        if (state == LandmarkState.Selected) {
            return@SetChangeListener
        }

        if (e.wasAdded() && e.elementAdded == landmark.uid) {
            highlightShape(HighlightingAnimationDuration)
        }

        if (e.wasRemoved() && e.elementRemoved == landmark.uid) {
            unhighlightShape(HighlightingAnimationDuration)
        }
    }
    private val weakHoveredLandmarksChangedEventHandler = WeakSetChangeListener(hoveredLandmarksChangedEventHandler)

    private val useOneColorChangedListener = InvalidationListener { useOneColorChanged() }
    private val weakUseOneColorChangedListener = WeakInvalidationListener(useOneColorChangedListener)

    private val commonUseCommonColorChangedEventHandler = ChangeListener { _, _, newCommonColor ->
        useCommonColorChanged(newCommonColor)
    }
    private val weakUseCommonColorChangedEventHandler = WeakChangeListener(commonUseCommonColorChangedEventHandler)


    init {
        addListeners()
    }

    open fun dispose() {
        removeListeners()
    }

    // Set up common shape properties
    // Can not be called during LandmarkView initialization because shape is created by inheritors
    protected fun setUpShape(shape: Shape, uid: Long) {
        shape.parentProperty().addListener(parentChangedListener)

        if (layerState.selectedLandmarksUids.contains(uid)) {
            state = LandmarkState.Selected
        }

        shape.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            when (state) {
                LandmarkState.Ordinary -> {
                    layerState.selectedLandmarksUids.add(uid)
                }
                LandmarkState.Selected -> {
                    layerState.selectedLandmarksUids.remove(uid)
                }
            }
        }

        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            layerState.hoveredLandmarksUids.add(uid)
        }

        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            layerState.hoveredLandmarksUids.remove(uid)
        }
    }

    protected fun toFront(node: Node) {
        node.viewOrder -= HIGHLIGHTING_VIEW_ORDER_GAP
    }

    protected fun toBack(node: Node) {
        node.viewOrder += HIGHLIGHTING_VIEW_ORDER_GAP
    }

    protected fun isHighlighted(landmark: Landmark) =
        layerState.selectedLandmarksUids.contains(landmark.uid)
                || layerState.hoveredLandmarksUids.contains(landmark.uid)

    protected abstract fun useOneColorChanged()

    protected abstract fun useCommonColorChanged(newCommonColor: Color)

    protected abstract fun highlightShape(duration: Duration)
    protected abstract fun unhighlightShape(duration: Duration)

    protected fun setShapeColor(shape: Shape, newColor: Color) {
        shape.fill = newColor
    }

    protected fun initializeCommonSettingsBindings(landmarkNode: Node) {
        // Does not cause memory leaks, as it uses a weak listener internally.
        landmarkNode.visibleWhen(landmark.layerSettings.enabledProperty)
    }

    protected abstract fun scaleChanged()

    private fun addListeners() {
        landmark.layerState.selectedLandmarksUids.addListener(weakSelectedLandmarksChangedEventHandler)
        landmark.layerState.hoveredLandmarksUids.addListener(weakHoveredLandmarksChangedEventHandler)

        landmark.layerSettings.useOneColorProperty.addListener(weakUseOneColorChangedListener)
        layerSettings.commonColorProperty.addListener(weakUseCommonColorChangedEventHandler)
    }

    private fun removeListeners() {
        layerState.selectedLandmarksUids.removeListener(weakSelectedLandmarksChangedEventHandler)
        layerState.hoveredLandmarksUids.removeListener(weakHoveredLandmarksChangedEventHandler)

        layerSettings.useOneColorProperty.removeListener(weakUseOneColorChangedListener)
        layerSettings.commonColorProperty.removeListener(weakUseCommonColorChangedEventHandler)
    }
}
