package solve.scene.view

import solve.scene.model.Landmark

class PlaneView(
    private val plane: Landmark.Plane,
    scale: Double,
) : LandmarkView(scale, plane) {
    override val node = null

    override fun drawOnCanvas(canvas: BufferedImageView) {
        val color = plane.layerSettings.getColor(plane)
        canvas.drawPoints(color, plane.points)
    }

    override fun scaleChanged() {
    }

    override fun highlightShape() {
    }

    override fun unhighlightShape() {
    }
}