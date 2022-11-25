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

    var scale: Double = scale
        set(value) {
            field = value
            scaleChanged()
        }

    protected fun setUpShape(shape: Shape) {
        shape.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            eventManager.landmarkSelected.invoke(
                LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
            )
        }
        shape.addEventHandler(MouseEvent.MOUSE_EXITED) {
            eventManager.landmarkUnselected.invoke(
                LandmarkEventArgs(landmark.uid, landmark.layer, frameTimestamp)
            )
        }
    }

    protected abstract fun scaleChanged()

    abstract fun select()
    abstract fun unselect()
    abstract fun highlight()
    abstract fun unhighlight()
}