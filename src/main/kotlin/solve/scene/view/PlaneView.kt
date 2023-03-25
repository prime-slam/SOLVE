package solve.scene.view

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.model.Point
import solve.scene.view.drawing.FrameDrawer
import solve.scene.view.drawing.FrameElement
import solve.scene.view.utils.createColorTimeline

class PlaneView(
    private val plane: Landmark.Plane,
    private val frameDrawer: FrameDrawer,
    private val canvasNode: Node,
    viewOrder: Int,
    scale: Double
) : LandmarkView(scale, viewOrder, plane) {
    override val node = null

    private class PlaneFrameElement(viewOrder: Int, override val points: List<Point>, initialColor: Color) :
        FrameElement(viewOrder) {
        var color: Color = initialColor

        fun changeViewOrder(value: Int) {
            viewOrder = FrameDrawer.LANDMARKS_VIEW_ORDER + value
        }

        override fun getColor(point: Point) = color
    }

    private val planeElement = PlaneFrameElement(viewOrder, plane.points, getColorWithOpacity())

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
        addListeners()
        if (isSelected) {
            highlightShapeIfNeeded(InstantAnimationDuration)
        }
    }

    override fun dispose() {
        super.dispose()
        removeListeners()
    }

    override fun addToFrameDrawer() {
        frameDrawer.addOrUpdateElement(planeElement)
    }

    override fun useCommonColorChanged() {
        planeElement.color = plane.layerSettings.getColor(plane)
        addToFrameDrawer()
    }

    override fun viewOrderChanged() {
        planeElement.changeViewOrder(viewOrder)
    }

    override fun commonColorChanged(newCommonColor: Color) {
        if (plane.layerSettings.useCommonColor) {
            planeElement.color = plane.layerSettings.getColor(plane)
            addToFrameDrawer()
        }
    }

    override fun scaleChanged() {
    }

    override fun highlightShape(duration: Duration) {
        val initialRgb = plane.layerSettings.getColor(plane)
        val targetRgb = plane.layerSettings.getUniqueColor(plane)
        val initialColor = Color(initialRgb.red, initialRgb.green, initialRgb.blue, plane.layerSettings.opacity)
        val targetColor = Color(targetRgb.red, targetRgb.green, targetRgb.blue, plane.layerSettings.opacity / 2)

        val timeline = createColorTimeline(duration, initialColor, targetColor) { color ->
            planeElement.color = color
            frameDrawer.addOrUpdateElement(planeElement)
            frameDrawer.redrawPoints(planeElement.points)
        }

        timeline.play()
    }

    override fun unhighlightShape(duration: Duration) {
        val initialRgb = plane.layerSettings.getUniqueColor(plane)
        val targetRgb = plane.layerSettings.getColor(plane)
        val initialColor = Color(initialRgb.red, initialRgb.green, initialRgb.blue, plane.layerSettings.opacity / 2)
        val targetColor = Color(targetRgb.red, targetRgb.green, targetRgb.blue, plane.layerSettings.opacity)

        val timeline = createColorTimeline(duration, initialColor, targetColor) { color ->
            planeElement.color = color
            frameDrawer.addOrUpdateElement(planeElement)
            frameDrawer.redrawPoints(planeElement.points)
        }

        timeline.play()
    }

    private fun isMouseOver(mouse: MouseEvent): Boolean {
        val mouseX = (mouse.x / scale).toInt().toShort()
        val mouseY = (mouse.y / scale).toInt().toShort()
        return plane.points.any { point -> point.x == mouseX && point.y == mouseY }
    }

    private fun addListeners() {
        canvasNode.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler)
    }

    private fun removeListeners() {
        canvasNode.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler)
    }

    private fun getColorWithOpacity(): Color {
        val rgb = plane.layerSettings.getColor(plane)
        return Color(rgb.red, rgb.green, rgb.blue, plane.layerSettings.opacity)
    }
}
