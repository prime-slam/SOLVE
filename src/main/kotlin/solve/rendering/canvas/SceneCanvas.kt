package solve.rendering.canvas

import org.joml.Vector2f
import org.joml.Vector2i
import solve.rendering.engine.core.renderers.FramesRenderer
import solve.rendering.engine.core.renderers.LinesLayerRenderer
import solve.rendering.engine.core.renderers.PlanesLayerRenderer
import solve.rendering.engine.core.renderers.PointsLayerRenderer
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.times
import solve.rendering.engine.utils.toFloatVector
import solve.scene.controller.SceneController
import solve.scene.model.Layer
import solve.scene.model.Scene
import solve.scene.model.VisualizationFrame
import solve.utils.ServiceLocator
import solve.utils.ceilToInt
import solve.rendering.engine.scene.Scene as EngineScene

class SceneCanvas : OpenGLCanvas() {
    private var sceneController: SceneController? = null
    private var canvasScene: EngineScene? = null

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

    private var needToReinitializeRenderers = false
    private var scene: Scene? = null

    private var isFirstFramesSelection = false

    init {
        initializeCanvasEvents()
    }

    fun setNewScene(scene: Scene) {
        this.scene = scene
        this.framesSize = Vector2i(scene.frameSize.width.toInt(), scene.frameSize.height.toInt())
        canvasScene?.framesRenderer?.setNewSceneFrames(scene.frames, framesSize.toFloatVector())
        isFirstFramesSelection = true

        needToReinitializeRenderers = true
    }

    fun setFramesSelection(framesSelection: List<VisualizationFrame>) {
        if (framesSelection.isNotEmpty() && isFirstFramesSelection)
        {
            canvasScene?.initializeFramesRenderer()
            isFirstFramesSelection = false
        }

        recalculateCameraCornersPositions()
        window.camera.position = leftUpperCornerCameraPosition

        canvasScene?.framesRenderer?.setFramesSelection(framesSelection)
        canvasScene?.landmarkRenderers?.forEach { renderer ->
            val rendererLayerSettings =
                canvasScene?.landmarkLayerRendererLayers?.get(renderer)?.settings ?: return@forEach
            val selectedFramesLayers =
                scene?.getLayersWithCommonSettings(rendererLayerSettings, framesSelection) ?: return@forEach
            renderer.setFramesSelectionLayers(selectedFramesLayers)
        }
        framesSelectionSize = framesSelection.count()
    }

    fun setColumnsNumber(columnsNumber: Int) {
        canvasScene?.framesRenderer?.setGridWidth(columnsNumber)
        canvasScene?.landmarkRenderers?.forEach { it.setNewGridWidth(columnsNumber) }
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
        super.onInit()
        val controller = ServiceLocator.getService<SceneController>() ?: return
        sceneController = controller

        canvasScene = EngineScene(FramesRenderer(window))
    }

    override fun onDraw(deltaTime: Float) {
        checkRenderersInitialization()

        if (needToReinitializeRenderers)
            return

        canvasScene?.update()
    }

    private fun checkRenderersInitialization() {
        if (needToReinitializeRenderers) {
            reinitializeRenderers()
        }
    }

    private fun reinitializeRenderers() {
        canvasScene?.clearLandmarkRenderers()
        val scene = this.scene ?: return

        scene.layers.forEach { layer ->
            addLandmarkRenderer(layer, scene)
        }
        canvasScene?.landmarkRenderers?.forEach {
            it.setNewGridWidth(sceneController?.installedColumnsNumber ?: SceneController.MaxColumnsNumber)
        }
        needToReinitializeRenderers = false
    }

    private fun addLandmarkRenderer(layer: Layer, scene: Scene) {
        val addingRenderer = when (layer) {
            is Layer.PointLayer -> PointsLayerRenderer(window) { sceneController?.scene }
            is Layer.LineLayer -> LinesLayerRenderer(window) { sceneController?.scene }
            is Layer.PlaneLayer -> PlanesLayerRenderer(window) { sceneController?.scene }
        }
        addingRenderer.setNewSceneFrames(scene.frames, framesSize.toFloatVector())
        canvasScene?.addLandmarkRenderer(addingRenderer, layer)
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
