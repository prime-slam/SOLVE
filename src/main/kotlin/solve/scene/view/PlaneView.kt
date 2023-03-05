package solve.scene.view

import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.util.Duration
import solve.scene.model.Landmark

class PlaneView(
    private val plane: Landmark.Plane,
    private val canvas: BufferedImageView,
    scale: Double,
) : LandmarkView(scale, plane) {
    override val node = null

    override fun drawOnCanvas() {
        val color = plane.layerSettings.getColor(plane)
        val colorWithOpacity = Color(color.red, color.green, color.blue, plane.layerSettings.opacity)
        canvas.drawPoints(colorWithOpacity, plane.points)
    }

    override fun useOneColorChanged() {
        drawOnCanvas()
    }

    override fun scaleChanged() {
    }

    override fun highlightShape(duration: Duration) {
        println("highlight plane")
    }

    override fun unhighlightShape(duration: Duration) {
        println("unhighlight plane")
    }

    private fun setUpCanvas() {
//        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED) {
//            when (state) {
//                LandmarkState.Ordinary -> {
//                    layerState.selectedLandmarksUids.add(uid)
//                }
//                LandmarkState.Selected -> {
//                    layerState.selectedLandmarksUids.remove(uid)
//                }
//            }
//        }
//
//        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED) {
//            layerState.hoveredLandmarksUids.add(uid)
//        }
//
//        canvas.addEventHandler(MouseEvent.MOUSE_EXITED) {
//            layerState.hoveredLandmarksUids.remove(uid)
//        }
    }
}