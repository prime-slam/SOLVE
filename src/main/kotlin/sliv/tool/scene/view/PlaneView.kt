package sliv.tool.scene.view

import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import sliv.tool.scene.model.Landmark

class PlaneView(gc: GraphicsContext, private val plane: Landmark.Plane) : LandmarkView(gc) {
    override fun draw() {
        gc.fill = Color.RED //TODO: use some colorizing delegate here
        gc.globalAlpha = plane.layer.opacity
        when (state) {
            LandmarkState.Ordinary -> {
                plane.points.forEach {
                    gc.fillRect(it.x.toDouble(), it.y.toDouble(), 1.0, 1.0)
                }
            }

            LandmarkState.Hovered -> TODO("Draw hovered plane")
        }
    }

    override fun updateIsHovered(event: MouseEvent) {
        TODO("Check if the point within the plane")
    }
}