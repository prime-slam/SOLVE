package sliv.tool.scene.view

import javafx.scene.canvas.GraphicsContext
import sliv.tool.scene.model.*

// Landmarks are not presented as visual node, this class is responsive for drawing and styling for landmarks
// This has access to landmark data class and its layer
// Due to possible performance issues it is not allowed to handle any events or receive any notifications
sealed class LandmarkView {
    companion object {
        fun create(landmark: Landmark): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(landmark)
                is Landmark.Line -> LineView(landmark)
                is Landmark.Plane -> PlaneView(landmark)
            }
        }
    }

    var state = LandmarkState.Ordinary
        protected set

    abstract fun draw(gc: GraphicsContext, scale: Double)

    fun updateIsHovered(x: Double, y: Double, scale: Double) {
        if (x < 0 || y < 0) {
            state = LandmarkState.Ordinary
            return
        }

        state = if (isHovered(x, y, scale)) {
            LandmarkState.Hovered
        } else {
            LandmarkState.Ordinary
        }
    }

    protected abstract fun isHovered(x: Double, y: Double, scale: Double): Boolean
}