package solve.scene.view

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

    override fun useCommonColorChanged(newCommonColor: Color) {
        TODO("Not yet implemented")
    }

    override fun scaleChanged() {
    }

    override fun highlightShape(duration: Duration) {
    }

    override fun unhighlightShape(duration: Duration) {
    }
}
