package solve.scene.view

import javafx.event.WeakEventHandler
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

    private val mouseClickedHandler = WeakEventHandler<MouseEvent> { mouse ->
        if (!isMouseOver(mouse)) {
            return@WeakEventHandler
        }
        if (isSelected) {
            plane.layerState.selectedLandmarksUids.remove(plane.uid)
            return@WeakEventHandler
        }
        plane.layerState.selectedLandmarksUids.add(plane.uid)
    }

    private val mouseMovedHandler = WeakEventHandler<MouseEvent> { mouse ->
        if (isHovered && !isMouseOver(mouse)) {
            plane.layerState.hoveredLandmarksUids.remove(plane.uid)
            return@WeakEventHandler
        }

        if (!isHovered && isMouseOver(mouse)) {
            plane.layerState.hoveredLandmarksUids.add(plane.uid)
        }
    }

    private val mouseExitedHandler = WeakEventHandler<MouseEvent> {
        if (isHovered) {
            plane.layerState.hoveredLandmarksUids.remove(plane.uid)
        }
    }

    init {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler)
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler)
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler)
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

    override fun useOneColorChanged() {
        drawOnCanvas()
    }

    override fun scaleChanged() {
    }

    override fun highlightShape(duration: Duration) {
        println("highlight plane ${plane.uid}}")
    }

    override fun unhighlightShape(duration: Duration) {
        println("unhighlight plane ${plane.uid}")
    }

    private fun isMouseOver(mouse: MouseEvent): Boolean {
        val mouseX = mouse.x.toInt().toShort()
        val mouseY = mouse.y.toInt().toShort()
        return plane.points.any { point -> point.x == mouseX && point.y == mouseY }
    }
}