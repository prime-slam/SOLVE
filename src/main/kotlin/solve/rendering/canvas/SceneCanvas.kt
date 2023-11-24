package solve.rendering.canvas

import javafx.beans.InvalidationListener
import javafx.event.EventHandler
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import org.joml.Vector2f
import org.joml.Vector2i
import solve.rendering.engine.rendering.renderers.FramesRenderer
import solve.rendering.engine.scene.Scene
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.toFloatVector
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.utils.ServiceLocator
import kotlin.math.pow
import kotlin.math.sign

class SceneCanvas : OpenGLCanvas() {
    private var sceneController: SceneController? = null
    private var framesRenderer: FramesRenderer? = null
    private var canvasScene: Scene? = null

    private var mousePosition = Vector2i()

    private var isDraggingScene = false
    private var dragStartCameraPosition = Vector2f()
    private var dragStartPoint = Vector2i()

    private val projectChangedEventHandler = InvalidationListener {
        val controller = sceneController ?: return@InvalidationListener
        framesRenderer?.setNewSceneFrames(controller.scene.frames)
    }
    private val framesChangedEventHandler = InvalidationListener {
        val controller = sceneController ?: return@InvalidationListener
        framesRenderer?.setFramesSelection(controller.scene.frames)
    }

    private val wheelScrolledEventHandler = EventHandler<ScrollEvent> { event ->
        val scrollValueSign = event.deltaY.sign.toInt()
        onWheelScrolled(scrollValueSign)
    }
    private val mouseMovedEventHandler = EventHandler<MouseEvent> { event ->
        mousePosition = extrudeEventMousePosition(event)
    }
    private val mouseDraggedEventHandler = EventHandler<MouseEvent> { event ->
        val mousePosition = extrudeEventMousePosition(event)
        if (event.button == MouseButton.MIDDLE) {
            onMouseDragged(mousePosition)
        }
    }
    private val wheelPressedEventHandler = EventHandler<MouseEvent> { event ->
        if (event.button != MouseButton.MIDDLE) {
            return@EventHandler
        }
        dragStartCameraPosition = window.camera.position
        dragStartPoint = fromScreenToCameraPoint(mousePosition)
        isDraggingScene = true
    }
    private val wheelReleasedEventHandler = EventHandler<MouseEvent> { event ->
        if (event.button != MouseButton.MIDDLE) {
            return@EventHandler
        }
        isDraggingScene = false
    }

    init {
        initializeCanvasEvents()
    }

    override fun onInit() {
        moveCameraToLeftUpperCorner()

        val controller = ServiceLocator.getService<SceneController>() ?: return
        sceneController = controller

        val renderer = FramesRenderer(window)
        framesRenderer = renderer
        canvasScene = Scene(listOf(renderer))

        addBindings()
    }

    override fun onDraw(deltaTime: Float) {
        canvasScene?.renderers?.forEach { it.render() }
    }

    override fun onDispose() {
        removeBindings()
    }

    private fun onMouseDragged(mousePosition: Vector2i) {
        val mousePoint = fromScreenToCameraPoint(mousePosition)
        if (isDraggingScene) {
            val dragVector = mousePoint - dragStartPoint
            window.camera.position = dragStartCameraPosition - dragVector.toFloatVector() / window.camera.scaledZoom
        }
    }

    private fun onWheelScrolled(scrollValueSign: Int) {
        val pointPosition = fromScreenToCameraPoint(mousePosition)
        val zoomMultiplier = OnWheelScrolledZoomMultiplier.pow(scrollValueSign)
        window.camera.zoomToPoint(pointPosition, zoomMultiplier)
    }

    private fun extrudeEventMousePosition(event: MouseEvent) = Vector2i(event.x.toInt(), event.y.toInt())

    private fun fromScreenToCameraPoint(screenPoint: Vector2i) = screenPoint - (window.size / 2)

    private fun moveCameraToLeftUpperCorner() {
        window.camera.position =
            Vector2f(window.width.toFloat(), window.height.toFloat()) / (2f * window.camera.scaledZoom)
    }

    private fun addBindings() {
        SceneFacade.lastVisualizationKeepSettingsProperty.addListener(projectChangedEventHandler)
        sceneController?.sceneProperty?.addListener(framesChangedEventHandler)

        canvas.onMouseMoved = mouseMovedEventHandler
        canvas.onMouseDragged = mouseDraggedEventHandler
        canvas.onMousePressed = wheelPressedEventHandler
        canvas.onMouseReleased = wheelReleasedEventHandler
        canvas.onScroll = wheelScrolledEventHandler
    }

    private fun removeBindings() {
        SceneFacade.lastVisualizationKeepSettingsProperty.removeListener(projectChangedEventHandler)
        sceneController?.sceneProperty?.removeListener(framesChangedEventHandler)

        canvas.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedEventHandler)
        canvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedEventHandler)
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, wheelPressedEventHandler)
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, wheelReleasedEventHandler)
        canvas.removeEventHandler(ScrollEvent.SCROLL, wheelScrolledEventHandler)
    }

    companion object {
        private const val OnWheelScrolledZoomMultiplier = 1.1f
    }
}
