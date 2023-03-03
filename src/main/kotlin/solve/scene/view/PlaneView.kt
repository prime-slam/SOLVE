package solve.scene.view

import javafx.scene.control.Label
import javafx.scene.paint.Color
import solve.scene.model.Landmark
import solve.utils.structures.Point

class PlaneView(
    private val plane: Landmark.Plane,
    scale: Double,
) : LandmarkView(scale, plane) {
    override val node = createUIDLabel()
    // TODO: add a line drawing implementation.
    override var lastEnabledColor: Color? = plane.layerSettings.colorManager.getColor(plane.uid)

    override fun drawOnCanvas(canvas: BufferedImageView) {
        val color = plane.layerSettings.colorManager.getColor(plane.uid)
        val colorWithOpacity = Color(color.red, color.green, color.blue, plane.layerSettings.opacity)
        canvas.drawPoints(colorWithOpacity, plane.points)
    }

    override fun scaleChanged() {
        updateLabelPosition(node)
    }

    override fun highlightShape() {
    }

    override fun unhighlightShape() {
    }

    private fun createUIDLabel(): Label {
        val label = Label(plane.uid.toString())
        updateLabelPosition(label)

        return label
    }

    private fun updateLabelPosition(label: Label) {
        val labelPosition = calculatePlaneCenterPoint() * scale

        label.layoutX = labelPosition.x
        label.layoutY = labelPosition.y
    }

    private fun calculatePlaneCenterPoint(): Point =
        Point(plane.points.map { it.x }.average(), plane.points.map { it.y }.average())
}