package solve.rendering.canvas

import io.github.palexdev.materialfx.controls.MFXContextMenu
import javafx.scene.Node
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import org.joml.Vector2f
import org.joml.Vector2i
import solve.parsers.planes.ImagePlanesParser
import solve.rendering.engine.Window
import solve.rendering.engine.core.input.MouseButton
import solve.rendering.engine.core.input.MouseInputHandler
import solve.rendering.engine.core.renderers.FramesRenderer
import solve.rendering.engine.core.renderers.LinesLayerRenderer
import solve.rendering.engine.core.renderers.PlanesLayerRenderer
import solve.rendering.engine.core.renderers.PointAssociationsRenderer
import solve.rendering.engine.core.renderers.PointsLayerRenderer
import solve.rendering.engine.core.renderers.Renderer
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.plus
import solve.rendering.engine.utils.times
import solve.rendering.engine.utils.toFloatVector
import solve.rendering.engine.utils.toIntVector
import solve.scene.controller.SceneController
import solve.scene.model.Landmark
import solve.scene.model.Layer
import solve.scene.model.LayerState
import solve.scene.model.Point
import solve.scene.model.Scene
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.AssociationAdorner
import solve.scene.view.association.AssociationManager
import solve.utils.ServiceLocator
import solve.utils.action
import solve.utils.ceilToInt
import solve.utils.getScreenPosition
import solve.utils.item
import solve.utils.lineSeparator
import solve.utils.removeSafely
import solve.utils.structures.DoublePoint
import tornadofx.*
import solve.rendering.engine.scene.Scene as EngineScene

/**
 * Aggregates logic related to scene control.
 * Provides interface for managing frames and changing visualization settings.
 */
class SceneCanvas : OpenGLCanvas() {
    val displayScale: Float
        get () = canvas.scene?.window?.renderScaleX?.toFloat() ?: 1f

    val scaledIdentityFramesSizeScale: Float
        get() = IdentityFramesSizeScale * displayScale

    private var sceneController: SceneController? = null
    private var engineScene: EngineScene? = null

    private var isDraggingScene = false
    private var dragStartCameraPoint = Vector2f()
    private var dragStartPoint = Vector2i()

    private var leftUpperCornerCameraPosition = Vector2f()
    private var rightLowerCornerCameraPosition = Vector2f()

    private var lastFramesSelection = listOf<VisualizationFrame>()
    private var framesSelectionSize = 0
    private var framesSize = Vector2i()
    private val framesRatio: Float
        get() = framesSize.x.toFloat() / framesSize.y
    private var columnsNumber = 0
    private val rowsNumber: Int
        get() = (framesSelectionSize.toFloat() / columnsNumber.toFloat()).ceilToInt()

    private var needToReinitializeRenderers = false
    private var scene: Scene? = null

    private var isFirstFramesSelection = false

    private var contextMenuInvokeFrame: VisualizationFrame? = null
    private var contextMenuInvokeFrameIndex = 0

    private val associationManager = AssociationManager()
    private var firstAssociatingFrameIndex = 0
    private var isFirstAssociatingFrameChosen = false
    private var associationAdorner: AssociationAdorner? = null

    private var contextMenu = buildContextMenu(canvas)

    init {
        initializeCanvasEvents()
        ServiceLocator.registerService(this)
    }

    fun setNewScene(scene: Scene) {
        this.scene = scene
        this.framesSize = Vector2i(scene.frameSize.width.toInt(), scene.frameSize.height.toInt())
        engineScene?.setNewScene(scene)
        isFirstFramesSelection = true
        needToReinitializeRenderers = true
        contextMenu = buildContextMenu(canvas)
        removeAssociationsAdorner()
    }

    fun setFramesSelection(framesSelection: List<VisualizationFrame>) {
        if (framesSelection.isNotEmpty() && isFirstFramesSelection) {
            isFirstFramesSelection = false
            if (scene?.layers?.any { it is Layer.PlanesLayer } ?: true) {
                engineScene?.initializeFramesRenderer()
            }
        }

        recalculateCameraCornersPositions()
        window.camera.position = leftUpperCornerCameraPosition

        engineScene?.setFramesSelection(framesSelection)
        associationManager.setFramesSelection(framesSelection)
        framesSelectionSize = framesSelection.count()
        lastFramesSelection = framesSelection
        removeAssociationsAdorner()
    }

    fun setColumnsNumber(columnsNumber: Int) {
        this.columnsNumber = columnsNumber
        engineScene?.setColumnsNumber(columnsNumber)
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

    fun handleClick(screenPoint: Vector2i, mouseButton: MouseButton) {
        // Coordinates in the frame coordinate system.
        val frameCoordinates = determineFrameCoordinates(screenPoint)
        val frameIndexCoordinates = frameCoordinates.toIntVector()
        val frameIndex = frameIndexCoordinates.y * columnsNumber + frameIndexCoordinates.x
        // Frame local coordinates (within a frame) including spacing area.
        var frameLocalCoordinates = Vector2f(frameCoordinates.x % 1, frameCoordinates.y % 1)
        // Frame local coordinates excluding spacing area.
        frameLocalCoordinates = frameToShaderVector(frameLocalCoordinates, framesSize)
        frameLocalCoordinates.x /= framesRatio
        val framePixelCoordinate =
            Vector2f(frameLocalCoordinates.x * framesSize.x, frameLocalCoordinates.y * framesSize.y).toIntVector()

        if (mouseButton == landmarkInteractionMouseButton) {
            if (isFirstAssociatingFrameChosen) {
                onSecondAssociatingFrameChosen(frameIndex)
                return
            }

            tryInteractWithLandmark(frameIndex, framePixelCoordinate)
        } else if (mouseButton == contextMenuMouseButton) {
            contextMenuInvokeFrame = getContextMenuInvokeFrame(frameIndex) ?: return
            contextMenuInvokeFrameIndex = frameIndex
            val canvasScreenPosition = canvas.getScreenPosition()
            contextMenu.show(
                canvas.scene.window,
                canvasScreenPosition.x + screenPoint.x.toDouble() / displayScale,
                canvasScreenPosition.y + screenPoint.y.toDouble() / displayScale
            )
        }
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
        val framesRenderer = FramesRenderer(window)
        val pointAssociationsRenderer = PointAssociationsRenderer(window, associationManager)

        engineScene = EngineScene(
            framesRenderer,
            pointAssociationsRenderer
        ) { scene }
    }

    override fun onDraw(deltaTime: Float) {
        checkRenderersInitialization()

        if (needToReinitializeRenderers) {
            return
        }

        engineScene?.update()
    }

    private fun determineFrameCoordinates(screenPoint: Vector2i): Vector2f {
        return shaderToFrameVector(window.calculateTopLeftCornerShaderPosition(), framesSize) +
            screenToFrameVector(screenPoint, framesSize, window)
    }

    private fun tryInteractWithLandmark(frameIndex: Int, frameInteractionPixel: Vector2i) {
        if (frameIndex >= lastFramesSelection.count()) {
            return
        }

        val interactingVisualizationFrame = lastFramesSelection[frameIndex]
        var clickedLandmarkLayerState: LayerState? = null
        var clickedLandmarkUID = 0L

        run clickHandler@{
            interactingVisualizationFrame.layers.forEach { layer ->
                if (!layer.settings.enabled) {
                    return@forEach
                }

                when (layer) {
                    is Layer.PointLayer -> {
                        val clickedLandmark = getClickedPointLandmark(layer, frameInteractionPixel) ?: return@forEach
                        clickedLandmarkLayerState = clickedLandmark.layerState
                        clickedLandmarkUID = clickedLandmark.uid
                        return@clickHandler
                    }

                    is Layer.LineLayer -> {
                        val clickedLandmark = getClickedLineLandmark(layer, frameInteractionPixel) ?: return@forEach
                        clickedLandmarkLayerState = clickedLandmark.layerState
                        clickedLandmarkUID = clickedLandmark.uid
                        return@clickHandler
                    }

                    is Layer.PlanesLayer -> {
                        clickedLandmarkUID =
                            getClickedPlaneUID(layer, frameInteractionPixel, true) ?: return@forEach
                        clickedLandmarkLayerState = layer.layerState
                        return@clickHandler
                    }
                }
            }
        }

        if (clickedLandmarkLayerState == null) {
            run clickHandler@{
                interactingVisualizationFrame.layers.filterIsInstance<Layer.PlanesLayer>().asReversed()
                    .forEach { layer ->
                        if (!layer.settings.enabled) {
                            return@forEach
                        }

                        clickedLandmarkUID =
                            getClickedPlaneUID(layer, frameInteractionPixel, false) ?: return@forEach
                        clickedLandmarkLayerState = layer.layerState
                        return@clickHandler
                    }
            }
        }

        val selectedLandmarkLayerState = clickedLandmarkLayerState ?: return

        if (selectedLandmarkLayerState.selectedLandmarksUIDs.contains(clickedLandmarkUID)) {
            selectedLandmarkLayerState.deselectLandmark(clickedLandmarkUID)
        } else {
            selectedLandmarkLayerState.selectLandmark(clickedLandmarkUID)
        }
    }

    private fun getClickedPointLandmark(layer: Layer.PointLayer, frameInteractionPixel: Vector2i): Landmark.Keypoint? {
        val pointLandmarks = layer.getLandmarks()
        val landmarkIndex =
            MouseInputHandler.indexOfClickedPointLandmark(
                pointLandmarks,
                frameInteractionPixel,
                layer.settings.selectedRadius.toFloat() / window.camera.zoom
            )

        if (landmarkIndex == -1) {
            return null
        }

        return pointLandmarks[landmarkIndex]
    }

    private fun getClickedLineLandmark(layer: Layer.LineLayer, frameInteractionPixel: Vector2i): Landmark.Line? {
        val lineLandmarks = layer.getLandmarks()
        val landmarkIndex =
            MouseInputHandler.indexOfClickedLineLandmark(
                lineLandmarks,
                frameInteractionPixel,
                layer.settings.selectedWidth.toFloat() / window.camera.zoom
            )

        if (landmarkIndex == -1) {
            return null
        }

        return lineLandmarks[landmarkIndex]
    }

    private fun getClickedPlaneUID(
        layer: Layer.PlanesLayer,
        frameInteractionPixel: Vector2i,
        ignoreSelectedPlanes: Boolean
    ): Long? {
        val framePlaneUIDs = ImagePlanesParser.extractUIDs(layer.filePath.toString())
        val clickedPixelIntegerColor = ImagePlanesParser.getPixelColor(
            layer.filePath.toString(),
            Point(frameInteractionPixel.x.toShort(), frameInteractionPixel.y.toShort())
        )?.toLong() ?: return null

        if (ignoreSelectedPlanes && layer.layerState.selectedLandmarksUIDs.contains(clickedPixelIntegerColor)) {
            return null
        }

        // The integer pixel color is equal to UID of the corresponding plane.
        if (!framePlaneUIDs.contains(clickedPixelIntegerColor)) {
            return null
        }

        return clickedPixelIntegerColor
    }

    private fun checkRenderersInitialization() {
        if (needToReinitializeRenderers) {
            reinitializeRenderers()
        }
    }

    private fun reinitializeRenderers() {
        engineScene?.clearLandmarkRenderers()
        val scene = this.scene ?: return

        scene.layers.forEach { layer ->
            addLandmarkRenderer(layer, scene)
        }
        engineScene?.landmarkRenderers?.forEach {
            it.setNewGridWidth(sceneController?.installedColumnsNumber ?: SceneController.MaxColumnsNumber)
        }
        needToReinitializeRenderers = false
    }

    private fun addLandmarkRenderer(layer: Layer, scene: Scene) {
        val addingRenderer = when (layer) {
            is Layer.PointLayer -> PointsLayerRenderer(window) { sceneController?.scene }
            is Layer.LineLayer -> LinesLayerRenderer(window) { sceneController?.scene }
            is Layer.PlanesLayer -> PlanesLayerRenderer(window) { sceneController?.scene }
        }
        addingRenderer.setNewSceneFrames(scene.frames, framesSize.toFloatVector())
        engineScene?.addLandmarkRenderer(addingRenderer, layer)
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

        val framesSelectionSize = Vector2f(
            columnsNumber * framesSize.x + (columnsNumber - 1) * Renderer.getSpacingWidth(framesSize),
            rowsNumber * framesSize.y + (rowsNumber - 1) * Renderer.getSpacingWidth(framesSize)
        )
        val framesSelectionScreenSize =
            framesSelectionSize * window.camera.zoom / scaledIdentityFramesSizeScale / window.camera.scaledZoom

        rightLowerCornerCameraPosition = framesSelectionScreenSize - leftUpperCornerCameraPosition

        rightLowerCornerCameraPosition.x =
            rightLowerCornerCameraPosition.x.coerceAtLeast(leftUpperCornerCameraPosition.x)
        rightLowerCornerCameraPosition.y =
            rightLowerCornerCameraPosition.y.coerceAtLeast(leftUpperCornerCameraPosition.y)
    }

    private fun buildContextMenu(parent: Node): MFXContextMenu {
        val contextMenu = MFXContextMenu(parent)
        contextMenu.addCopyTimestampItem()
        contextMenu.lineSeparator()
        if (scene?.layers?.any { it is Layer.PointLayer } ?: false) {
            contextMenu.addAssociateKeypointsItem()
        }
        contextMenu.addClearAssociationsItem()
        isFirstAssociatingFrameChosen = false

        return contextMenu
    }

    private fun MFXContextMenu.addCopyTimestampItem() {
        item("Copy timestamp").action {
            val invokeFrame = contextMenuInvokeFrame ?: return@action

            val timestamp = invokeFrame.timestamp
            val clipboardContent = ClipboardContent().also { it.putString(timestamp.toString()) }
            Clipboard.getSystemClipboard().setContent(clipboardContent)
        }
    }

    private fun MFXContextMenu.addAssociateKeypointsItem() {
        item("Associate keypoints").action {
            onFirstAssociatingFrameChosen(contextMenuInvokeFrameIndex)
        }
    }

    private fun onFirstAssociatingFrameChosen(firstFrameIndex: Int) {
        firstAssociatingFrameIndex = contextMenuInvokeFrameIndex
        isFirstAssociatingFrameChosen = true
        createAssociationAdorner(firstFrameIndex)
    }

    private fun onSecondAssociatingFrameChosen(secondFrameIndex: Int) {
        if (!isFirstAssociatingFrameChosen) {
            return
        }

        isFirstAssociatingFrameChosen = false
        removeAssociationsAdorner()

        if (firstAssociatingFrameIndex != secondFrameIndex && secondFrameIndex in lastFramesSelection.indices) {
            associationManager.associate(firstAssociatingFrameIndex, secondFrameIndex)
        }
    }

    private fun removeAssociationsAdorner() {
        val removingAssociationAdorner = associationAdorner ?: return
        canvas.removeSafely(removingAssociationAdorner.node)
        removingAssociationAdorner.destroy()
    }

    private fun createAssociationAdorner(firstFrameIndex: Int) {
        val getFrameScreenPosition = {
            val frameIndexCoordinates = Vector2i(firstFrameIndex % columnsNumber, firstFrameIndex / columnsNumber)
            val frameTopLeftRelativeScreenPosition = getFrameTopLeftCornerRelativeScreenPosition(
                frameIndexCoordinates,
                framesSize,
                window
            )

            DoublePoint(
                frameTopLeftRelativeScreenPosition.x.toDouble(),
                frameTopLeftRelativeScreenPosition.y.toDouble()
            )
        }
        val getScreenScale = {
            window.camera.zoom.toDouble() / scaledIdentityFramesSizeScale
        }
        val associationAdorner = AssociationAdorner(
            framesSize.x.toDouble(),
            framesSize.y.toDouble(),
            getFrameScreenPosition,
            getScreenScale
        )
        canvas.add(associationAdorner.node)
        this.associationAdorner = associationAdorner
    }

    private fun MFXContextMenu.addClearAssociationsItem() {
        item("Clear associations").action {
            if (associationManager.associationConnections.any {
                it.firstFrameIndex == contextMenuInvokeFrameIndex || it.secondFrameIndex == contextMenuInvokeFrameIndex
            }
            ) {
                associationManager.clearAssociation(contextMenuInvokeFrameIndex)
            }
        }
    }

    private fun getContextMenuInvokeFrame(contextMenuInvokeFrameIndex: Int): VisualizationFrame? {
        if (contextMenuInvokeFrameIndex >= lastFramesSelection.count()) {
            return null
        }

        return lastFramesSelection[contextMenuInvokeFrameIndex]
    }

    companion object {
        const val IdentityFramesSizeScale = 1.605f

        private val landmarkInteractionMouseButton = MouseButton.Left
        private val contextMenuMouseButton = MouseButton.Right

        // Used for frame to shader and shader to frame vector conversions.
        private fun getFrameToShaderConversionCoefficients(framesSize: Vector2i): Vector2f {
            return Vector2f(
                (framesSize.x + Renderer.getSpacingWidth(framesSize)) / framesSize.y,
                (1 + Renderer.FramesSpacing)
            )
        }

        // Converts a screen vector to frame coordinates.
        // One frame including spacing area corresponds to a (1, 1) frame vector.
        fun screenToFrameVector(screenVector: Vector2i, framesSize: Vector2i, window: Window): Vector2f {
            val shaderVector = window.screenToShaderVector(screenVector)
            val frameVector = shaderToFrameVector(shaderVector, framesSize)

            return frameVector
        }

        fun shaderToFrameVector(shaderVector: Vector2f, framesSize: Vector2i): Vector2f {
            val frameVector = Vector2f(shaderVector)
            val frameToShaderMultiplier = getFrameToShaderConversionCoefficients(framesSize)
            frameVector.x /= frameToShaderMultiplier.x
            frameVector.y /= frameToShaderMultiplier.y

            return frameVector
        }

        fun frameToShaderVector(frameVector: Vector2f, framesSize: Vector2i): Vector2f {
            val shaderVector = Vector2f(frameVector)
            val frameToShaderMultiplier = getFrameToShaderConversionCoefficients(framesSize)
            shaderVector.x *= frameToShaderMultiplier.x
            shaderVector.y *= frameToShaderMultiplier.y

            return shaderVector
        }

        // Returns a frame position in screen coordinates without taking into account the camera position.
        fun getGlobalFrameScreenPosition(frameIndexCoordinates: Vector2i, framesSize: Vector2i): Vector2f {
            return Vector2f(
                (framesSize.x.toFloat() + Renderer.getSpacingWidth(framesSize)) * frameIndexCoordinates.x,
                (framesSize.y.toFloat() * (1f + Renderer.FramesSpacing)) * frameIndexCoordinates.y
            )
        }

        // Returns a top left corner position in screen coordinates without taking into account the camera position.
        fun getGlobalTopLeftCornerScreenPosition(window: Window, framesSize: Vector2i): Vector2f {
            val topLeftCornerShaderPosition = window.calculateTopLeftCornerShaderPosition()
            return shaderToFrameVector(topLeftCornerShaderPosition, framesSize).also {
                it.x *= framesSize.x
                it.y *= framesSize.y
            }
        }

        // Returns a frame position in screen coordinates with taking into account the camera position.
        fun getFrameTopLeftCornerRelativeScreenPosition(
            frameIndexCoordinates: Vector2i,
            framesSize: Vector2i,
            window: Window
        ): Vector2f {
            val globalFrameScreenPosition = getGlobalFrameScreenPosition(frameIndexCoordinates, framesSize)
            val globalTopLeftCornerScreenPosition = getGlobalTopLeftCornerScreenPosition(window, framesSize)

            return globalFrameScreenPosition - globalTopLeftCornerScreenPosition
        }
    }
}
