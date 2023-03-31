package solve.scene.view

import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.Point
import solve.scene.view.drawing.CanvasEventHandler
import solve.scene.view.drawing.FrameDrawer
import solve.scene.view.drawing.FrameElement
import solve.scene.view.drawing.FrameEventManager
import solve.scene.view.utils.createColorTimeline
import solve.utils.clearChildren
import solve.utils.getBlackOrWhiteContrastingTo
import solve.utils.withReplacedOpacity
import tornadofx.add
import solve.utils.structures.DoublePoint as DoublePoint

class PlaneView(
    private val plane: Landmark.Plane,
    private val frameDrawer: FrameDrawer,
    private val frameEventManager: FrameEventManager,
    viewOrder: Int,
    scale: Double
) : LandmarkView(scale, viewOrder, plane) {
    companion object {
        private const val UIDLabelFontSize = 12.0
        private const val OpacityUnhighlightCoefficient = 0.5

        private const val PlaneUIDLabelSpawnDelayMillis = 25L
    }

    private class PlaneFrameElement(viewOrder: Int, override val points: List<Point>, initialColor: Color) :
        FrameElement(viewOrder) {
        var color: Color = initialColor

        fun changeViewOrder(value: Int) {
            viewOrder = FrameDrawer.LANDMARKS_VIEW_ORDER + value
        }

        override fun getColor(point: Point) = color
    }

    private var planeUIDLabel: Label? = null
    override val node = HBox().also { it.isMouseTransparent = true }

    private var planeCenterPoint: DoublePoint? = null
    private val uidLabelCoordinates: DoublePoint
        get() = (planeCenterPoint ?: DoublePoint(0.0, 0.0)) * scale
    private var isShowingUIDLabel = false

    private val planeElement = PlaneFrameElement(
        viewOrder,
        plane.points,
        if (plane.layerSettings.enabled) getColorWithOpacity() else getDisabledPlaneColor()
    )

    private var lastEnabledPlaneElementColor = planeElement.color

    private var mousePressedFramePosition: DoublePoint? = null

    private val mousePressedHandler = EventHandler<MouseEvent> { mouse ->
        if (!plane.layerSettings.enabled) {
            return@EventHandler
        }
        mouse.consume()
        mousePressedFramePosition = frameDrawer.screenPosition
    }
    private val mousePressedCanvasEventHandler = CanvasEventHandler(planeElement, mousePressedHandler)

    private val mouseReleasedHandler = EventHandler<MouseEvent> { mouse ->
        if (!plane.layerSettings.enabled) {
            return@EventHandler
        }
        mouse.consume()
        val mousePressedCanvasPosition = mousePressedFramePosition ?: return@EventHandler
        val maxDistance = 2.0
        if (mousePressedCanvasPosition.distanceTo(frameDrawer.screenPosition) > maxDistance) {
            return@EventHandler
        }
        if (isSelected) {
            plane.layerState.selectedLandmarksUids.remove(plane.uid)
            return@EventHandler
        }
        plane.layerState.selectedLandmarksUids.add(plane.uid)
    }
    private val mouseReleasedCanvasEventHandler = CanvasEventHandler(planeElement, mouseReleasedHandler)

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
        setPlaneElementColor(plane.layerSettings.getColor(plane))
    }

    override fun viewOrderChanged() {
        planeElement.changeViewOrder(viewOrder)
    }

    override fun commonColorChanged(newCommonColor: Color) {
        if (plane.layerSettings.useCommonColor) {
            setPlaneElementColor(plane.layerSettings.getColor(plane))
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
        val targetColor = initialColor.withReplacedOpacity(initialColor.opacity * OpacityUnhighlightCoefficient)

        val timeline = createColorTimeline(duration, initialColor, targetColor) { color ->
            lastEnabledPlaneElementColor = color
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
            lastEnabledPlaneElementColor = color
            redrawPlaneWithColor(color)
        }

        timeline.play()
    }

    private fun addListeners() {
        frameEventManager.subscribeMousePressed(mousePressedCanvasEventHandler)
        frameEventManager.subscribeMouseReleased(mouseReleasedCanvasEventHandler)

        plane.layerSettings.enabledProperty.addListener(weakEnabledChangedEventHandler)
    }

    private fun removeListeners() {
        frameEventManager.unsubscribeMousePressed(mousePressedCanvasEventHandler)
        frameEventManager.unsubscribeMouseReleased(mouseReleasedCanvasEventHandler)

        plane.layerSettings.enabledProperty.removeListener(weakEnabledChangedEventHandler)
    }

    private fun getColorWithOpacity() = plane.layerSettings.getColorWithOpacity(plane)

    private fun setPlaneElementColor(newColor: Color) {
        planeElement.color = newColor
        addToFrameDrawer()
    }

    private fun redrawPlaneWithColor(newColor: Color) {
        setPlaneElementColor(newColor)
        frameDrawer.redrawPoints(planeElement.points)
    }

    private fun getDisabledPlaneColor() = getColorWithOpacity().withReplacedOpacity(LayerSettings.MinOpacity)

    private fun onEnabledChanged() {
        lateinit var planeVisibilityColor: Color

        if (plane.layerSettings.enabled) {
            planeVisibilityColor = lastEnabledPlaneElementColor
            planeUIDLabel?.isVisible = true
        } else {
            planeVisibilityColor = Color.TRANSPARENT
            planeUIDLabel?.isVisible = false
        }

        redrawPlaneWithColor(planeVisibilityColor)
    }

    private fun createUIDLabel(): Label {
        val uidLabel = Label(plane.uid.toString())
        uidLabel.textFill = getBlackOrWhiteContrastingTo(plane.layerSettings.getColor(plane))
        uidLabel.font = Font.font(null, FontWeight.BOLD, UIDLabelFontSize)

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
        node.layoutX = uidLabelCoordinates.x - (planeUIDLabel?.width ?: 0.0) / 2.0
        node.layoutY = uidLabelCoordinates.y - (planeUIDLabel?.height ?: 0.0) / 2.0
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
        if (planeUIDLabel == null) {
            planeUIDLabel = createUIDLabel()
        }
        val uidLabel = planeUIDLabel ?: return

        uidLabel.isVisible = false
        node.add(this.planeUIDLabel ?: return)

        // Without delay the spawn coordinates of the uid label are incorrect.
        val uidSpawnCoroutineScope = CoroutineScope(Dispatchers.JavaFx)
        uidSpawnCoroutineScope.launch(Dispatchers.JavaFx) {
            delay(PlaneUIDLabelSpawnDelayMillis)
            updateUIDLabelPosition()
            uidLabel.isVisible = true
            isShowingUIDLabel = true
        }
    }
}
