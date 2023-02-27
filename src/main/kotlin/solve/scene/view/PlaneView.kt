package solve.scene.view

import javafx.scene.paint.Color
import solve.scene.model.Landmark

class PlaneView(
    private val plane: Landmark.Plane,
    scale: Double,
) : LandmarkView(scale, plane) {
    override val node = null
    // TODO: add a line drawing implementation.
    override var lastEnabledColor: Color? = plane.layerSettings.colorManager.getColor(plane.uid)

    override fun drawOnCanvas(canvas: BufferedImageView) {
        val color = plane.layerSettings.colorManager.getColor(plane.uid)
        val colorWithOpacity = Color(color.red, color.green, color.blue, plane.layerSettings.opacity)
        canvas.drawPoints(colorWithOpacity, plane.points)
    }

    override fun scaleChanged() {
    }

    override fun highlightShape() {
    }

    override fun unhighlightShape() {
    }
}