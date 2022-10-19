package sliv.tool.scene.view

import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import sliv.tool.scene.model.*

// Landmarks are not presented as visual node, this class is responsive for drawing and styling for landmarks
// This has access to landmark data class and its layer
// Due to possible performance issues it is not allowed to handle any events or receive any notifications
sealed class LandmarkView(protected val gc: GraphicsContext) {
    companion object {
        fun create(gc: GraphicsContext, landmark: Landmark): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(gc, landmark)
                is Landmark.Line -> LineView(gc, landmark)
                is Landmark.Plane -> PlaneView(gc, landmark)
            }
        }
    }

    var state = LandmarkState.Ordinary
        protected set

    abstract fun draw()
    abstract fun updateIsHovered(event: MouseEvent)
}