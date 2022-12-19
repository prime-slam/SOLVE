package solve.scene.view

import javafx.collections.SetChangeListener
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

    init {
        landmark.layerSettings.selectedLandmarksUids.addListener(selectedLandmarksChangedEventHandler)
        landmark.layerSettings.hoveredLandmarksUids.addListener(hoveredLandmarksChangedEventHandler)
    }

    fun dispose() {
        landmark.layerSettings.selectedLandmarksUids.removeListener(selectedLandmarksChangedEventHandler)
        landmark.layerSettings.hoveredLandmarksUids.removeListener(hoveredLandmarksChangedEventHandler)
    }

    // Set up common shape properties
    // Can not be called during LandmarkView initialization because shape is created by inheritors
    protected fun setUpShape(shape: Shape) {
        if (landmark.layerSettings.selectedLandmarksUids.contains(landmark.uid)) {
            state = LandmarkState.Selected
        }

        shape.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            when (state) {
                LandmarkState.Ordinary -> {
                    landmark.layerSettings.selectedLandmarksUids.add(landmark.uid)
                }
                LandmarkState.Selected -> {
                    landmark.layerSettings.selectedLandmarksUids.remove(landmark.uid)
                }
            }
        }

        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            landmark.layerSettings.hoveredLandmarksUids.add(landmark.uid)
        }

        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            landmark.layerSettings.hoveredLandmarksUids.remove(landmark.uid)
        }
    }

    protected abstract fun scaleChanged()

    protected abstract fun highlightShape()
    protected abstract fun unhighlightShape()
}