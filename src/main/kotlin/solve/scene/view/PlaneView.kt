package solve.scene.view

import javafx.scene.shape.Shape
import solve.scene.model.Landmark

class PlaneView(
    private val plane: Landmark.Plane,
    scale: Double,
) : LandmarkView(scale, plane) {
    override val node: Shape
        get() = TODO("Not yet implemented")

    override fun scaleChanged() {
        TODO("Not yet implemented")
    }

    override fun highlightShape() {
        TODO("Not yet implemented")
    }

    override fun unhighlightShape() {
        TODO("Not yet implemented")
    }
}