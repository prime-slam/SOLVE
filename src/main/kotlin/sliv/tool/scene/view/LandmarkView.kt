package sliv.tool.scene.view

import javafx.scene.shape.Shape
import sliv.tool.scene.model.*

// Responsive for creating and setting visual effects for landmarks presenting controls
// This has access to landmark data class and its layer
sealed class LandmarkView(scale: Double, val landmark: Landmark) {
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

    abstract fun select()
    abstract fun unselect()
    abstract fun highlight()
    abstract fun unhighlight()
}