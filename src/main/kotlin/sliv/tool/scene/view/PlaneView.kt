package sliv.tool.scene.view

import javafx.scene.shape.Shape
import sliv.tool.scene.model.Landmark

class PlaneView(
    private val plane: Landmark.Plane,
    scale: Double,
    frameTimestamp: Long,
    stateSynchronizationManager: LandmarkStateSynchronizationManager
) : LandmarkView(scale, plane, frameTimestamp, stateSynchronizationManager) {
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