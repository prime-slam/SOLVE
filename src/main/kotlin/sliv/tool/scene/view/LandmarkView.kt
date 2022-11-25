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
    private val eventManager: FramesEventManager
) {
    companion object {
        fun create(landmark: Landmark, scale: Double, frameTimestamp: Long, eventManager: FramesEventManager): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(landmark, scale, frameTimestamp, eventManager)
                is Landmark.Line -> LineView(landmark, scale, frameTimestamp, eventManager)
                is Landmark.Plane -> PlaneView(landmark, scale, frameTimestamp, eventManager)
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
        if (frameTimestamp != eventArgs.frameTimestamp && eventArgs.uid == landmark.uid) {
            selectShape()
        }
    }

    private val landmarkUnselectedEventHandler: (LandmarkEventArgs) -> Unit = { eventArgs ->
        if (frameTimestamp != eventArgs.frameTimestamp && eventArgs.uid == landmark.uid) {
            unselectShape()
        }
    }

    private val landmarkMouseEnteredEventHandler: (LandmarkEventArgs) -> Unit = { eventArgs ->
        if (frameTimestamp != eventArgs.frameTimestamp && eventArgs.uid == landmark.uid) {
            handleMouseEntered()
        }
    }

    private val landmarkMouseExitedEventHandler: (LandmarkEventArgs) -> Unit = { eventArgs ->
        if (frameTimestamp != eventArgs.frameTimestamp && eventArgs.uid == landmark.uid) {
            handleMouseExited()
        }
    }

    init {
        eventManager.landmarkSelected += landmarkSelectedEventHandler
        eventManager.landmarkUnselected += landmarkUnselectedEventHandler
        eventManager.landmarkMouseEntered += landmarkMouseEnteredEventHandler
        eventManager.landmarkMouseExited += landmarkMouseExitedEventHandler
    }

    fun dispose() {
        eventManager.landmarkSelected -= landmarkSelectedEventHandler
        eventManager.landmarkUnselected -= landmarkUnselectedEventHandler
        eventManager.landmarkMouseEntered -= landmarkMouseEnteredEventHandler
        eventManager.landmarkMouseExited -= landmarkMouseExitedEventHandler
    }

    protected fun setUpShape(shape: Shape) {
        shape.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            when (state) {
                LandmarkState.Ordinary -> {
                    selectShape()
                    eventManager.landmarkSelected.invoke(
                        LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
                    )
                }
                LandmarkState.Selected -> {
                    unselectShape()
                    eventManager.landmarkUnselected.invoke(
                        LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
                    )
                }
            }
        }

        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            if (state == LandmarkState.Selected) {
                return@addEventHandler
            }

            handleMouseEntered()
            eventManager.landmarkMouseEntered.invoke(
                LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
            )
        }

        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            if (state == LandmarkState.Selected) {
                return@addEventHandler
            }

            handleMouseExited()
            eventManager.landmarkMouseExited.invoke(
                LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
            )
        }
    }

    private fun selectShape() {
        state = LandmarkState.Selected
        highlightShape()
    }

    private fun unselectShape() {
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