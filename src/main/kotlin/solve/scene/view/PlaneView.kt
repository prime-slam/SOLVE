package solve.scene.view

import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.util.Duration
import solve.scene.model.DefaultOpacity
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.Point
import solve.scene.view.drawing.FrameDrawer
import solve.scene.view.drawing.FrameElement
import solve.scene.view.utils.createColorTimeline
import solve.utils.clearChildren
import solve.utils.getBlackOrWhiteContrastingTo
import solve.utils.getScreenPosition
import tornadofx.*
import solve.utils.structures.DoublePoint as DoublePoint

class PlaneView(
    private val plane: Landmark.Plane,
    private val frameDrawer: FrameDrawer,
    private val canvasNode: Node,
    viewOrder: Int,
    scale: Double
) : LandmarkView(scale, viewOrder, plane) {
    private var uidLabel: Label? = null
    override val node = HBox()

    private var planeCenterPoint: DoublePoint? = null
    private val uidLabelCoordinates: DoublePoint
        get() = (planeCenterPoint ?: DoublePoint(0.0, 0.0)) * scale
    private var isShowingUIDLabel = false

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
        if (isShowingUIDLabel) {
            updateUIDLabelPosition()
        }
    }

    private var highlightingAnimation: Timeline? = null

    override fun highlightShape(duration: Duration) {
        showUIDLabel()

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
        hideUIDLabel()

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

    private fun createUIDLabel(): Label {
        val uidLabel = Label(plane.uid.toString())
        uidLabel.textFill = getBlackOrWhiteContrastingTo(plane.layerSettings.getColor(plane))

        return uidLabel
    }

    private fun calculatePlaneCenterPoint(): DoublePoint {
        val centroidPoint = DoublePoint(plane.points.map { it.x }.average(), plane.points.map { it.y }.average())
        val nearestToCentroid = plane.points.minBy { point ->
            DoublePoint(point.x.toDouble(), point.y.toDouble()).distanceTo(centroidPoint)
        }

        return DoublePoint(nearestToCentroid.x.toDouble(), nearestToCentroid.y.toDouble())
    }

    private fun updateUIDLabelPosition() {
        node.layoutX = uidLabelCoordinates.x - (uidLabel?.width ?: 0.0) / 2.0
        node.layoutY = uidLabelCoordinates.y - (uidLabel?.height ?: 0.0) / 2.0
    }

    private fun hideUIDLabel() {
        if (!isShowingUIDLabel) {
            return
        }

        node.clearChildren()
        isShowingUIDLabel = false
    }

    private fun showUIDLabel() {
        if (isShowingUIDLabel) {
            return
        }

        if (planeCenterPoint == null) {
            planeCenterPoint = calculatePlaneCenterPoint()
        }
        if (uidLabel == null) {
            uidLabel = createUIDLabel()
        }
        node.add(uidLabel ?: return)
        uidLabel?.isVisible = false

        Platform.runLater {
            updateUIDLabelPosition()
            Platform.runLater {
                updateUIDLabelPosition()
            }
            uidLabel?.isVisible = true
            isShowingUIDLabel = true
        }
    }
}
