package sliv.tool.scene.view

import javafx.scene.shape.Shape
import sliv.tool.scene.model.Landmark

class PlaneView(private val plane: Landmark.Plane, scale: Double) : LandmarkView(scale) {
    override val shape: Shape
        get() = TODO("Not yet implemented")

    override fun scaleChanged() {
        TODO("Not yet implemented")
    }
//    override fun draw(gc: GraphicsContext, scale: Double) {
//        gc.fill = Color.RED //TODO: use some colorizing delegate here
//        gc.globalAlpha = plane.layer.opacity
//        when (state) {
//            LandmarkState.Ordinary -> {
//                plane.points.forEach {
//                    gc.fillRect(it.x.toDouble() * scale, it.y.toDouble() * scale, scale, scale)
//                }
//            }
//
//            LandmarkState.Hovered -> TODO("Draw hovered plane")
//        }
//    }
//
//    override fun isHovered(x: Double, y: Double, scale: Double): Boolean {
//        TODO("Not yet implemented")
//    }
}