package solve.rendering.canvas

import org.joml.Vector2f
import org.joml.Vector2i
import solve.rendering.engine.rendering.renderers.FramesRenderer
import solve.rendering.engine.scene.Scene
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.toFloatVector
import solve.scene.controller.SceneController
import solve.scene.model.VisualizationFrame
import solve.utils.ServiceLocator

class SceneCanvas : OpenGLCanvas() {
    private var sceneController: SceneController? = null
    private var framesRenderer: FramesRenderer? = null
    private var canvasScene: Scene? = null

    private var isDraggingScene = false
    private var dragStartCameraPoint = Vector2f()
    private var dragStartPoint = Vector2i()


    init {
        initializeCanvasEvents()
    }

    fun setNewSceneFrames(frames: List<VisualizationFrame>) {
        framesRenderer?.setNewSceneFrames(frames)
    }

    fun setFramesSelection(framesSelection: List<VisualizationFrame>) {
        framesRenderer?.setFramesSelection(framesSelection)
    }

    fun setColumnsNumber(columnsNumber: Int) {
        framesRenderer?.setGridWidth(columnsNumber)
    }

    fun dragTo(toScreenPoint: Vector2i) {
        val mousePoint = fromScreenToCameraPoint(toScreenPoint)
        if (isDraggingScene) {
            val dragVector = mousePoint - dragStartPoint
            window.camera.position = dragStartCameraPoint - dragVector.toFloatVector() / window.camera.scaledZoom
        }
    }

    fun startDragging(fromScreenPoint: Vector2i) {
        dragStartCameraPoint = window.camera.position
        dragStartPoint = fromScreenToCameraPoint(fromScreenPoint)
        isDraggingScene = true
    }

    fun stopDragging() {
        isDraggingScene = false
    }

    fun zoomToPoint(screenPoint: Vector2i, newZoom: Float) {
        val cameraPoint = fromScreenToCameraPoint(screenPoint)
        window.camera.zoomToPoint(cameraPoint, newZoom)
    }

    override fun onInit() {
        moveCameraToLeftUpperCorner()

        val controller = ServiceLocator.getService<SceneController>() ?: return
        sceneController = controller

        val renderer = FramesRenderer(window)
        framesRenderer = renderer
        canvasScene = Scene(listOf(renderer))
    }

    override fun onDraw(deltaTime: Float) {
        canvasScene?.renderers?.forEach { it.render() }
    }

    private fun fromScreenToCameraPoint(screenPoint: Vector2i) = screenPoint - (window.size / 2)

    private fun moveCameraToLeftUpperCorner() {
        window.camera.position =
            Vector2f(window.width.toFloat(), window.height.toFloat()) / (2f * window.camera.scaledZoom)
    }



    companion object {
        private const val DefaultMinZoom = 0.1f
        private const val DefaultMaxZoom = 10f
    }
}
