package solve.scene.view

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.view.utils.createColorTimeline

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

    private val mouseMovedHandler = EventHandler<MouseEvent> { mouse ->
        if (isHovered && !isMouseOver(mouse)) {
            plane.layerState.hoveredLandmarksUids.remove(plane.uid)
            return@EventHandler
        }

        if (!isHovered && isMouseOver(mouse)) {
            plane.layerState.hoveredLandmarksUids.add(plane.uid)
        }
    }

    private val mouseExitedHandler = EventHandler<MouseEvent> {
        if (isHovered) {
            plane.layerState.hoveredLandmarksUids.remove(plane.uid)
        }
    }

    init {
        addListeners()
        if (shouldHighlight) {
            highlightShapeIfNeeded(InstantAnimationDuration)
        }
    }

    override fun dispose() {
        super.dispose()
        removeListeners()
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
        val initialRgb = plane.layerSettings.getColor(plane)
        val targetRgb = plane.layerSettings.getUniqueColor(plane)
        val initialColor = Color(initialRgb.red, initialRgb.green, initialRgb.blue, plane.layerSettings.opacity)
        val targetColor = Color(targetRgb.red, targetRgb.green, targetRgb.blue, plane.layerSettings.opacity / 2)

        val timeline = createColorTimeline(duration, initialColor, targetColor) { color ->
            canvas.drawPoints(color, plane.points)
        }

        timeline.play()
    }

    override fun unhighlightShape(duration: Duration) {
        val initialRgb = plane.layerSettings.getUniqueColor(plane)
        val targetRgb = plane.layerSettings.getColor(plane)
        val initialColor = Color(initialRgb.red, initialRgb.green, initialRgb.blue, plane.layerSettings.opacity / 2)
        val targetColor = Color(targetRgb.red, targetRgb.green, targetRgb.blue, plane.layerSettings.opacity)

        val timeline = createColorTimeline(duration, initialColor, targetColor) { color ->
            canvas.drawPoints(color, plane.points)
        }

        timeline.play()
    }

    private fun isMouseOver(mouse: MouseEvent): Boolean {
        val mouseX = (mouse.x / scale).toInt().toShort()
        val mouseY = (mouse.y / scale).toInt().toShort()
        return plane.points.any { point -> point.x == mouseX && point.y == mouseY }
    }

    private fun addListeners() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler)
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler)
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler)
    }

    private fun removeListeners() {
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler)
        canvas.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler)
        canvas.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler)
    }
}