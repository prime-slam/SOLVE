package sliv.tool.scene.view

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape
import sliv.tool.scene.model.Landmark

// Responsive for creating and setting visual effects for landmarks presenting controls
// This has access to landmark data class and its layer
sealed class LandmarkView(
    scale: Double,
    val landmark: Landmark,
    private val frameTimestamp: Long,
    private val stateSynchronizationManager: LandmarkStateSynchronizationManager
) {
    companion object {
        fun create(landmark: Landmark, scale: Double, frameTimestamp: Long, stateSynchronizationManager: LandmarkStateSynchronizationManager): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(landmark, scale, frameTimestamp, stateSynchronizationManager)
                is Landmark.Line -> LineView(landmark, scale, frameTimestamp, stateSynchronizationManager)
                is Landmark.Plane -> PlaneView(landmark, scale, frameTimestamp, stateSynchronizationManager)
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

    private val landmarkSelectedEventHandler: (LandmarkEventArgs) -> Unit = { eventArgs ->
        if (eventArgs.uid == landmark.uid) {
            select()
        }
    }

    private val landmarkUnselectedEventHandler: (LandmarkEventArgs) -> Unit = { eventArgs ->
        if (eventArgs.uid == landmark.uid) {
            unselect()
        }
    }

    private val landmarkMouseEnteredEventHandler: (LandmarkEventArgs) -> Unit = { eventArgs ->
        if (eventArgs.uid == landmark.uid) {
            handleMouseEntered()
        }
    }

    private val landmarkMouseExitedEventHandler: (LandmarkEventArgs) -> Unit = { eventArgs ->
        if (eventArgs.uid == landmark.uid) {
            handleMouseExited()
        }
    }

    init {
        stateSynchronizationManager.landmarkSelected += landmarkSelectedEventHandler
        stateSynchronizationManager.landmarkUnselected += landmarkUnselectedEventHandler
        stateSynchronizationManager.landmarkMouseEntered += landmarkMouseEnteredEventHandler
        stateSynchronizationManager.landmarkMouseExited += landmarkMouseExitedEventHandler
    }

    fun dispose() {
        stateSynchronizationManager.landmarkSelected -= landmarkSelectedEventHandler
        stateSynchronizationManager.landmarkUnselected -= landmarkUnselectedEventHandler
        stateSynchronizationManager.landmarkMouseEntered -= landmarkMouseEnteredEventHandler
        stateSynchronizationManager.landmarkMouseExited -= landmarkMouseExitedEventHandler
    }

    // Set up common shape properties
    // Can not be called during LandmarkView initialization because shape is created by inheritors
    protected fun setUpShape(shape: Shape) {
        if (stateSynchronizationManager.selectedLandmarksUids.contains(landmark.uid)) {
            state = LandmarkState.Selected
        }

        shape.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            when (state) {
                LandmarkState.Ordinary -> {
                    stateSynchronizationManager.landmarkSelected.invoke(
                        LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
                    )
                }
                LandmarkState.Selected -> {
                    stateSynchronizationManager.landmarkUnselected.invoke(
                        LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
                    )
                }
            }
        }

        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            stateSynchronizationManager.landmarkMouseEntered.invoke(
                LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
            )
        }

        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            stateSynchronizationManager.landmarkMouseExited.invoke(
                LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
            )
        }
    }

    private fun select() {
        state = LandmarkState.Selected
        highlightShape()
    }

    private fun unselect() {
        state = LandmarkState.Ordinary
        unhighlightShape()
    }

    private fun handleMouseEntered() {
        if (state == LandmarkState.Selected) {
            return
        }

        highlightShape()
    }

    private fun handleMouseExited() {
        if (state == LandmarkState.Selected) {
            return
        }

        unhighlightShape()
    }

    protected abstract fun scaleChanged()

    protected abstract fun highlightShape()
    protected abstract fun unhighlightShape()
}