package solve.scene.view

import javafx.event.EventHandler
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

    private val mouseClickedHandler = EventHandler<MouseEvent> { mouse ->
        if (!isMouseOver(mouse)) {
            return@EventHandler
        }
        if (isSelected) {
            plane.layerState.selectedLandmarksUids.remove(plane.uid)
            return@EventHandler
        }
        plane.layerState.selectedLandmarksUids.add(plane.uid)
    }

    init {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler)
    }

    override fun dispose() {
        super.dispose()
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler)
    }

    override fun drawOnCanvas() {
        val color = plane.layerSettings.getColor(plane)
        val colorWithOpacity = Color(color.red, color.green, color.blue, plane.layerSettings.opacity)
        canvas.drawPoints(colorWithOpacity, plane.points)
    }

    override fun useCommonColorChanged() {
        drawOnCanvas()
    }

    override fun commonColorChanged(newCommonColor: Color) {
        TODO("Not yet implemented")
    }

    override fun scaleChanged() {
    }

    override fun highlightShape(duration: Duration) {
    }

    override fun unhighlightShape(duration: Duration) {
    }

    private fun isMouseOver(mouse: MouseEvent): Boolean {
        val mouseX = mouse.x.toInt().toShort()
        val mouseY = mouse.y.toInt().toShort()
        return plane.points.any { point -> point.x == mouseX && point.y == mouseY }
    }
}
