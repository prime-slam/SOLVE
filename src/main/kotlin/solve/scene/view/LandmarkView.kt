package solve.scene.view

import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape
import solve.scene.model.Landmark

// Responsive for creating and setting visual effects for landmarks presenting controls
// This has access to landmark data class and its layer
sealed class LandmarkView(
    scale: Double,
    val landmark: Landmark,
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
    abstract val node: Node

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
        landmark.layerState.selectedLandmarksUids.addListener(weakSelectedLandmarksChangedEventHandler)
        landmark.layerState.hoveredLandmarksUids.addListener(weakHoveredLandmarksChangedEventHandler)
    }

    fun dispose() {
        landmark.layerState.selectedLandmarksUids.removeListener(weakSelectedLandmarksChangedEventHandler)
        landmark.layerState.hoveredLandmarksUids.removeListener(weakHoveredLandmarksChangedEventHandler)
    }

    // Set up common shape properties
    // Can not be called during LandmarkView initialization because shape is created by inheritors
    protected fun setUpShape(shape: Shape) {
        if (landmark.layerState.selectedLandmarksUids.contains(landmark.uid)) {
            state = LandmarkState.Selected
        }

        shape.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            when (state) {
                LandmarkState.Ordinary -> {
                    landmark.layerState.selectedLandmarksUids.add(landmark.uid)
                }
                LandmarkState.Selected -> {
                    landmark.layerState.selectedLandmarksUids.remove(landmark.uid)
                }
            }
        }

        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            landmark.layerState.hoveredLandmarksUids.add(landmark.uid)
        }

        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            landmark.layerState.hoveredLandmarksUids.remove(landmark.uid)
        }
    }

    protected abstract fun scaleChanged()

    protected abstract fun highlightShape()
    protected abstract fun unhighlightShape()
}