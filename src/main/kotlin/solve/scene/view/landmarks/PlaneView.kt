package solve.scene.view.landmarks

import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.util.Duration
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.Point
import solve.scene.view.drawing.CanvasEventHandler
import solve.scene.view.drawing.FrameDrawer
import solve.scene.view.drawing.FrameElement
import solve.scene.view.drawing.FrameEventManager
import solve.scene.view.utils.createColorTimeline
import solve.utils.structures.DoublePoint
import solve.utils.withReplacedOpacity

class PlaneView(
    private val plane: Landmark.Plane,
    private val frameDrawer: FrameDrawer,
    private val frameEventManager: FrameEventManager,
    viewOrder: Int,
    scale: Double
) : LandmarkView(scale, viewOrder, plane) {
    private class PlaneFrameElement(viewOrder: Int, override val points: List<Point>, initialColor: Color) :
        FrameElement(viewOrder) {
        var color: Color = initialColor

        fun changeViewOrder(value: Int) {
            viewOrder = FrameDrawer.LANDMARKS_VIEW_ORDER + value
        }

        override fun getColor(point: Point) = color
    }

    private var planeUIDLabel = PlaneUIDLabel(plane)

    override val node = planeUIDLabel.uidLabelNode

    private val planeElement = PlaneFrameElement(
        viewOrder,
        plane.points,
        if (plane.layerSettings.enabled) {
            getColorWithOpacity()
        } else {
            getDisabledPlaneColor()
        }
    )

    private var lastEnabledPlaneElementColor = planeElement.color

    private var mousePressedFramePosition: DoublePoint? = null

    private val mousePressedHandler = EventHandler<MouseEvent> {
        if (!plane.layerSettings.enabled) {
            return@EventHandler
        }
        mousePressedFramePosition = frameDrawer.screenPosition
    }
    private val mousePressedCanvasEventHandler = CanvasEventHandler(planeElement, mousePressedHandler)

    private val mouseReleasedHandler = EventHandler<MouseEvent> {
        if (!plane.layerSettings.enabled) {
            return@EventHandler
        }
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
        if (planeUIDLabel.isShowingLabel) {
            planeUIDLabel.updatePosition(scale)
        }
    }

    private var highlightingAnimation: Timeline? = null

    override fun highlightShape(duration: Duration) {
        planeUIDLabel.show(scale)

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
        planeUIDLabel.hide()

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

        plane.layerSettings.enabledProperty.addListener(enabledChangedEventHandler)
    }

    private fun removeListeners() {
        frameEventManager.unsubscribeMousePressed(mousePressedCanvasEventHandler)
        frameEventManager.unsubscribeMouseReleased(mouseReleasedCanvasEventHandler)

        plane.layerSettings.enabledProperty.removeListener(enabledChangedEventHandler)
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

        planeUIDLabel.enabled = plane.layerSettings.enabled
        planeVisibilityColor = if (plane.layerSettings.enabled) {
            lastEnabledPlaneElementColor
        } else {
            Color.TRANSPARENT
        }

        redrawPlaneWithColor(planeVisibilityColor)
    }

    companion object {
        private const val OpacityUnhighlightCoefficient = 0.5
    }
}
