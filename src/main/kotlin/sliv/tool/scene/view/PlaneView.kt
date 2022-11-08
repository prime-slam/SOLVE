package sliv.tool.scene.view

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import sliv.tool.scene.model.Landmark

class PlaneView(private val plane: Landmark.Plane) : LandmarkView() {
    override fun draw(gc: GraphicsContext, scale: Double) {
        gc.fill = Color.RED //TODO: use some colorizing delegate here
        gc.globalAlpha = plane.layer.opacity
        when (state) {
            LandmarkState.Ordinary -> {
                plane.points.forEach {
                    gc.fillRect(it.x.toDouble() * scale, it.y.toDouble() * scale, 1.0 * scale, 1.0 * scale)
                }
            }

            LandmarkState.Hovered -> TODO("Draw hovered plane")
        }
    }

    override fun isHovered(x: Double, y: Double, scale: Double): Boolean {
        TODO("Not yet implemented")
    }
}