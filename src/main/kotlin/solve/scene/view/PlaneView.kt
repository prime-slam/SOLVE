package solve.scene.view

import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.util.Duration
import solve.scene.model.DefaultOpacity
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.Point
import solve.scene.view.drawing.FrameDrawer
import solve.scene.view.drawing.FrameElement
import solve.scene.view.utils.createColorTimeline
import solve.utils.getScreenPosition
import solve.utils.structures.Point as DoublePoint

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

    private var mousePressedFramePosition: DoublePoint? = null

    private val mousePressedHandler = EventHandler<MouseEvent> { mouse ->
        if (!isMouseOver(mouse)) {
            return@EventHandler
        }
        mousePressedFramePosition = canvasNode.getScreenPosition()
    }

    private val mouseReleasedHandler = EventHandler<MouseEvent> { mouse ->
        if (!isMouseOver(mouse)) {
            mousePressedFramePosition = null
            return@EventHandler
        }
        val mousePressedCanvasPosition = mousePressedFramePosition ?: return@EventHandler
        val maxDistance = 2.0
        if (mousePressedCanvasPosition.distanceTo(canvasNode.getScreenPosition()) > maxDistance) {
            return@EventHandler
        }
        if (isSelected) {
            plane.layerState.selectedLandmarksUids.remove(plane.uid)
            return@EventHandler
        }
        plane.layerState.selectedLandmarksUids.add(plane.uid)
    }

    private val enabledChangedEventHandler = InvalidationListener {
        onEnabledChanged()
    }
    private val weakEnabledChangedEventHandler = WeakInvalidationListener(enabledChangedEventHandler)

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
        setPlaneColor(plane.layerSettings.getColor(plane))
    }

    override fun viewOrderChanged() {
        planeElement.changeViewOrder(viewOrder)
    }

    override fun commonColorChanged(newCommonColor: Color) {
        if (plane.layerSettings.useCommonColor) {
            setPlaneColor(plane.layerSettings.getColor(plane))
        }
    }

    override fun scaleChanged() {
    }

    private var highlightingAnimation: Timeline? = null

    override fun highlightShape(duration: Duration) {
        val initialColor = getColorWithOpacity()
        val targetColor =
            Color(initialColor.red, initialColor.green, initialColor.blue, initialColor.opacity / 2)

        val timeline = createColorTimeline(duration, initialColor, targetColor) { color ->
            redrawPlaneWithColor(color)
        }

        highlightingAnimation = timeline
        timeline.play()
    }

    override fun unhighlightShape(duration: Duration) {
        highlightingAnimation?.stop()
        highlightingAnimation = null

        val initialColor = planeElement.color
        val targetColor = getColorWithOpacity()

        val timeline = createColorTimeline(duration, initialColor, targetColor) { color ->
            redrawPlaneWithColor(color)
        }

        timeline.play()
    }

    private fun isMouseOver(mouse: MouseEvent): Boolean {
        val mouseX = (mouse.x / scale).toInt().toShort()
        val mouseY = (mouse.y / scale).toInt().toShort()
        return plane.points.any { point -> point.x == mouseX && point.y == mouseY }
    }

    private fun addListeners() {
        canvasNode.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler)
        canvasNode.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler)

        plane.layerSettings.enabledProperty.addListener(weakEnabledChangedEventHandler)
    }

    private fun removeListeners() {
        canvasNode.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler)
        canvasNode.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler)

        plane.layerSettings.enabledProperty.removeListener(weakEnabledChangedEventHandler)
    }

    private fun getColorWithOpacity() = plane.layerSettings.getColorWithOpacity(plane)

    private fun setPlaneColor(newColor: Color) {
        planeElement.color = newColor
        addToFrameDrawer()
    }

    private fun redrawPlaneWithColor(newColor: Color) {
        setPlaneColor(newColor)
        frameDrawer.redrawPoints(planeElement.points)
    }

    private fun onEnabledChanged() {
        if (plane.layerSettings.enabled) {
            plane.layerSettings.opacity = DefaultOpacity
        } else {
            plane.layerSettings.opacity = LayerSettings.MinOpacity
        }
        redrawPlaneWithColor(getColorWithOpacity())
    }
}
