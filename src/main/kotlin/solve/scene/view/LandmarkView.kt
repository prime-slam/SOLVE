package solve.scene.view

import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import solve.scene.model.Landmark
import tornadofx.visibleWhen

// Responsive for creating and setting visual effects for landmarks presenting controls
// This has access to landmark data class and its layer
sealed class LandmarkView(
    scale: Double,
    private val landmark: Landmark,
) {
    companion object {
        fun create(landmark: Landmark, scale: Double): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(landmark, scale)
                is Landmark.Line -> LineView(landmark, scale)
                is Landmark.Plane -> PlaneView(landmark, scale)
            }
        }
    }

    // When shape is created in an inheritor
    // setUpShape() should be called to set up common features for all landmarks
    abstract val node: Node?

    abstract fun drawOnCanvas(canvas: BufferedImageView)

    private val layerState = landmark.layerState

    private var state: LandmarkState = LandmarkState.Ordinary

    var scale: Double = scale
        set(value) {
            field = value
            scaleChanged()
        }

    // Should be stored to avoid weak listener from be collected
    private val selectedLandmarksChangedEventHandler = SetChangeListener<Long> { e ->
        if (e.wasAdded() && e.elementAdded == landmark.uid) {
            state = LandmarkState.Selected
            highlightShape()
        }

        if (e.wasRemoved() && e.elementRemoved == landmark.uid) {
            state = LandmarkState.Ordinary
            unhighlightShape()
        }
    }
    private val weakSelectedLandmarksChangedEventHandler = WeakSetChangeListener(selectedLandmarksChangedEventHandler)

    // Should be stored to avoid weak listener from be collected
    private val hoveredLandmarksChangedEventHandler = SetChangeListener<Long> { e ->
        if (state == LandmarkState.Selected) {
            return@SetChangeListener
        }

        if (e.wasAdded() && e.elementAdded == landmark.uid) {
            highlightShape()
        }

        if (e.wasRemoved() && e.elementRemoved == landmark.uid) {
            unhighlightShape()
        }
    }
    private val weakHoveredLandmarksChangedEventHandler = WeakSetChangeListener(hoveredLandmarksChangedEventHandler)

    init {
        addListeners()
    }

    fun dispose() {
        removeListeners()
    }

    // Set up common shape properties
    // Can not be called during LandmarkView initialization because shape is created by inheritors
    protected fun setUpShape(shape: Shape, uid: Long) {
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

    protected fun setShapeColor(shape: Shape, newColor: Color) {
        shape.fill = newColor
    }

    protected fun initializeCommonSettingsBindings(landmarkNode: Node) {
        landmarkNode.visibleWhen(landmark.layerSettings.enabledProperty)
    }

    protected abstract fun scaleChanged()

    protected abstract fun highlightShape()

    protected abstract fun unhighlightShape()

    private fun addListeners() {
        landmark.layerState.selectedLandmarksUids.addListener(weakSelectedLandmarksChangedEventHandler)
        landmark.layerState.hoveredLandmarksUids.addListener(weakHoveredLandmarksChangedEventHandler)
    }

    private fun removeListeners() {
        layerState.selectedLandmarksUids.removeListener(weakSelectedLandmarksChangedEventHandler)
        layerState.hoveredLandmarksUids.removeListener(weakHoveredLandmarksChangedEventHandler)
    }
}