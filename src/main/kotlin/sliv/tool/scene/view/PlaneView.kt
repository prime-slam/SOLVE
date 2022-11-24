package sliv.tool.scene.view

import javafx.scene.shape.Shape
import sliv.tool.scene.model.Landmark

class PlaneView(private val plane: Landmark.Plane, scale: Double) : LandmarkView(scale, plane) {
    override val shape: Shape
        get() = TODO("Not yet implemented")

    override fun scaleChanged() {
        TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
    }

    override fun unselect() {
        TODO("Not yet implemented")
    }
    override fun highlight() {
        TODO("Not yet implemented")
    }

    override fun unhighlight() {
        TODO("Not yet implemented")
    }
}