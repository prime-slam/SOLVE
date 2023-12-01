package solve.rendering.canvas

import javafx.application.Platform
import org.joml.Vector2f
import org.joml.Vector2i
import solve.rendering.engine.rendering.renderers.FramesRenderer
import solve.rendering.engine.scene.Scene
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.times
import solve.rendering.engine.utils.toFloatVector
import solve.scene.controller.SceneController
import solve.scene.model.VisualizationFrame
import solve.utils.ServiceLocator
import solve.utils.ceilToInt

class SceneCanvas : OpenGLCanvas() {
    private var sceneController: SceneController? = null
    private var framesRenderer: FramesRenderer? = null
    private var canvasScene: Scene? = null

    private var isDraggingScene = false
    private var dragStartCameraPoint = Vector2f()
    private var dragStartPoint = Vector2i()

    private var leftUpperCornerCameraPosition = Vector2f()
    private var rightLowerCornerCameraPosition = Vector2f()

    private var framesSelectionSize = 0
    private var framesSize = Vector2i()
    private var columnsNumber = 0
    private val rowsNumber: Int
        get() = (framesSelectionSize.toFloat() / columnsNumber.toFloat()).ceilToInt()

    init {
        initializeCanvasEvents()
    }

    fun setNewSceneFrames(frames: List<VisualizationFrame>, framesSize: Vector2i) {
        framesRenderer?.setNewSceneFrames(frames)
        this.framesSize = framesSize
    }

    fun setFramesSelection(framesSelection: List<VisualizationFrame>) {
        framesRenderer?.setFramesSelection(framesSelection)
        framesSelectionSize = framesSelection.count()
        Platform.runLater {
            recalculateCameraCornersPositions()
            window.camera.position = leftUpperCornerCameraPosition
        }
    }

    fun setColumnsNumber(columnsNumber: Int) {
        framesRenderer?.setGridWidth(columnsNumber)
        this.columnsNumber = columnsNumber
    }

    fun dragTo(toScreenPoint: Vector2i) {
        val mousePoint = fromScreenToCameraPoint(toScreenPoint)
        if (isDraggingScene) {
            val dragVector = mousePoint - dragStartPoint
            window.camera.position = dragStartCameraPoint - dragVector.toFloatVector() / window.camera.scaledZoom
            constraintCameraPosition()
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

        recalculateCameraCornersPositions()
        constraintCameraPosition()
    }

    override fun onInit() {
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

    private fun constraintCameraPosition() {
        window.camera.position.x =
            window.camera.position.x.coerceIn(leftUpperCornerCameraPosition.x, rightLowerCornerCameraPosition.x)
        window.camera.position.y =
            window.camera.position.y.coerceIn(leftUpperCornerCameraPosition.y, rightLowerCornerCameraPosition.y)
    }

    private fun recalculateCameraCornersPositions() {
        val halfScreenSize = (Vector2f(window.width.toFloat(), window.height.toFloat()) / 2f) / window.camera.scaledZoom
        leftUpperCornerCameraPosition = halfScreenSize

        val framesSelectionSize =
            Vector2i(columnsNumber * framesSize.x, rowsNumber * framesSize.y).toFloatVector()
        val framesSelectionScreenSize =
            framesSelectionSize * window.camera.zoom / IdentityFramesSizeScale / window.camera.scaledZoom

        rightLowerCornerCameraPosition = framesSelectionScreenSize - leftUpperCornerCameraPosition

        rightLowerCornerCameraPosition.x =
            rightLowerCornerCameraPosition.x.coerceAtLeast(leftUpperCornerCameraPosition.x)
        rightLowerCornerCameraPosition.y =
            rightLowerCornerCameraPosition.y.coerceAtLeast(leftUpperCornerCameraPosition.y)
    }

    companion object {
        const val IdentityFramesSizeScale = 1.605f
    }
}
