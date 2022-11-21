package sliv.tool.scene.view

import javafx.scene.shape.Shape
import sliv.tool.scene.model.*

// Landmarks are not presented as visual node, this class is responsive for drawing and styling for landmarks
// This has access to landmark data class and its layer
// Due to possible performance issues it is not allowed to handle any events or receive any notifications
sealed class LandmarkView(scale: Double) {
    companion object {
        fun create(landmark: Landmark, scale: Double): LandmarkView {
            return when (landmark) {
                is Landmark.Keypoint -> KeypointView(landmark, scale)
                is Landmark.Line -> LineView(landmark, scale)
                is Landmark.Plane -> PlaneView(landmark, scale)
            }
        }
    }

    abstract val shape: Shape
    var scale: Double = scale
        set(value) {
            field = value
            scaleChanged()
        }

    protected abstract fun scaleChanged()
}