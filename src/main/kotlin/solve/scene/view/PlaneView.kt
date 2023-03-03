package solve.scene.view

import javafx.scene.control.Label
import javafx.scene.paint.Color
import solve.scene.model.Landmark
import solve.utils.getBlackOrWhiteContrastingTo
import solve.utils.structures.Point

class PlaneView(
    private val plane: Landmark.Plane,
    scale: Double,
) : LandmarkView(scale, plane) {
    private val uidLabel = createUIDLabel()
    override val node = uidLabel

    private val planeCenterPoint = calculatePlaneCenterPoint()
    private val uidLabelCoordinates: Point
        get() = planeCenterPoint * scale

    override var lastEnabledColor: Color? = null

    private val color: Color
        get() = plane.layerSettings.colorManager.getColor(plane.uid)

    init {
        updateUIDLabelPosition()
    }

    override fun drawOnCanvas(canvas: BufferedImageView) {
        val colorWithOpacity = Color(color.red, color.green, color.blue, plane.layerSettings.opacity)
        canvas.drawPoints(colorWithOpacity, plane.points)
    }

    override fun scaleChanged() {
        updateUIDLabelPosition()
    }

    override fun highlightShape() {
    }

    override fun unhighlightShape() {
    }

    private fun createUIDLabel(): Label {
        val uidLabel = Label(plane.uid.toString())
        uidLabel.textFill = getBlackOrWhiteContrastingTo(color)

        return uidLabel
    }

    private fun calculatePlaneCenterPoint(): Point {
        return Point(plane.points.map { it.x }.average(), plane.points.map { it.y }.average())
    }

    private fun updateUIDLabelPosition() {
        val currentCoordinates = uidLabelCoordinates

        uidLabel.layoutX = currentCoordinates.x
        uidLabel.layoutY = currentCoordinates.y
    }
}