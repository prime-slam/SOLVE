package solve.scene.view

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import solve.scene.controller.SceneController
import solve.scene.view.association.AssociationsManager
import solve.scene.view.association.OutOfFramesLayer
import solve.scene.view.virtualizedfx.VirtualizedFXGridProvider
import tornadofx.View
import tornadofx.label
import tornadofx.onChange
import tornadofx.vbox
import solve.utils.structures.DoublePoint as DoublePoint
import solve.utils.structures.Size as DoubleSize

class SceneView : View() {
    private val controller: SceneController by inject()
    private var frameDataLoadingScope = CoroutineScope(Dispatchers.Default)
    private var currentGrid: Grid? = null
    private var frameViewCache: FrameViewCache? = null

    override val root = vbox {
        label("Empty scene placeholder")
    }

    init {
        initKeyboardNavigation()
        addBindings()
    }

    private fun draw() {
        unbindPositionProperties()
        currentGrid?.dispose()
        currentGrid = null
        root.children.clear()

        val scene = controller.sceneProperty.value

        if (scene.frames.isEmpty()) {
            label("No frames was provided")
            return
        }

        val frameSize = scene.frameSize
        val gridCellSize = DoubleSize(frameSize.width + framesMargin, frameSize.height + framesMargin)

        val columnsNumber = controller.columnsNumber

        // VirtualizedFX Grid assumes that frames count is a divider for the columns number
        val emptyFrames = (0 until (columnsNumber - scene.frames.count() % columnsNumber) % columnsNumber).map { null }
        val frames = scene.frames + emptyFrames

        val outOfFramesLayer = OutOfFramesLayer()
        val associationsManager = AssociationsManager(
            frameSize,
            framesMargin,
            controller.scaleProperty,
            scene.frames,
            columnsNumber,
            outOfFramesLayer
        )

        val frameViewParameters = FrameViewParameters(frameDataLoadingScope, associationsManager, scene)

        val canReuseCache =
            frameViewCache?.size == frameSize && frameViewCache?.canvasBufferDepth == scene.canvasLayersCount
        frameViewCache = if (canReuseCache) {
            frameViewCache!!
        } else {
            FrameViewCache(
                frameSize,
                scene.canvasLayersCount
            ) { frame, parameters ->
                FrameView(
                    frameSize,
                    controller.scaleProperty,
                    frameViewCache!!,
                    scene.canvasLayersCount,
                    parameters,
                    frame
                )
            }
        }
        val grid = VirtualizedFXGridProvider.createGrid(
            frames,
            columnsNumber,
            gridCellSize,
            controller.scaleProperty,
            outOfFramesLayer
        ) { frame ->
            frameViewCache!!.get(frame, frameViewParameters)
        }

        bindPositionProperties(grid)

        grid.setUpPanning()

        grid.setOnMouseWheel { event ->
            if (event.isConsumed) { // If event is consumed by vsp
                return@setOnMouseWheel
            }
            if (event.deltaY == 0.0) {
                return@setOnMouseWheel
            }
            val mousePosition = DoublePoint(event.x, event.y)
            if (event.deltaY > 0) {
                controller.zoomIn(mousePosition)
            } else {
                controller.zoomOut(mousePosition)
            }
        }

        currentGrid = grid
        add(grid.node)

        grid.node.requestFocus()
    }

    private fun bindPositionProperties(grid: Grid) {
        controller.xProperty.bind(grid.xProperty)
        controller.yProperty.bind(grid.yProperty)
        controller.scrollX = { newX -> grid.scrollX(newX) }
        controller.scrollY = { newY -> grid.scrollY(newY) }
    }

    private fun unbindPositionProperties() {
        controller.xProperty.unbind()
        controller.yProperty.unbind()
        controller.scrollX = null
        controller.scrollY = null
    }

    private fun addBindings() {
        controller.sceneWidthProperty.bind(root.widthProperty())
        controller.sceneProperty.onChange { scene ->
            if (scene != null) {
                frameDataLoadingScope.cancel()
                frameDataLoadingScope = CoroutineScope(Dispatchers.Default)
                Platform.runLater {
                    draw()
                }
            }
        }

        // Grid settings bindings.
        controller.installedColumnsNumberProperty.onChange { columnsNumber ->
            columnsNumber ?: return@onChange

            currentGrid?.changeColumnsNumber(columnsNumber)
        }
        Platform.runLater {
            currentWindow?.widthProperty()?.onChange {
                Platform.runLater {
                    controller.recalculateScale(true)
                }
            }
        }
    }

    private fun initKeyboardNavigation() {
        accelerators[KeyCodeCombination(KeyCode.RIGHT)] = handler@{
            scrollRight(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.KP_RIGHT)] = handler@{
            scrollRight(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.LEFT)] = handler@{
            scrollLeft(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.KP_LEFT)] = handler@{
            scrollLeft(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.UP)] = handler@{
            scrollUp(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.KP_UP)] = handler@{
            scrollUp(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.DOWN)] = handler@{
            scrollDown(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.KP_DOWN)] = handler@{
            scrollDown(currentGrid ?: return@handler)
        }

        accelerators[KeyCodeCombination(KeyCode.MINUS)] = handler@{
            if (currentGrid == null) return@handler
            zoomOutFromCenter()
        }

        accelerators[KeyCodeCombination(KeyCode.SUBTRACT)] = handler@{
            if (currentGrid == null) return@handler
            zoomOutFromCenter()
        }

        accelerators[KeyCodeCombination(KeyCode.EQUALS)] = handler@{
            if (currentGrid == null) return@handler
            zoomInToCenter()
        }

        accelerators[KeyCodeCombination(KeyCode.ADD)] = handler@{
            if (currentGrid == null) return@handler
            zoomInToCenter()
        }
    }

    private fun scrollRight(grid: Grid) = grid.scrollX(grid.xProperty.value + scrollSpeed)

    private fun scrollLeft(grid: Grid) = grid.scrollX(grid.xProperty.value - scrollSpeed)

    private fun scrollUp(grid: Grid) = grid.scrollY(grid.yProperty.value - scrollSpeed)

    private fun scrollDown(grid: Grid) = grid.scrollY(grid.yProperty.value + scrollSpeed)
    private fun zoomOutFromCenter() = controller.zoomOut(DoublePoint(root.width / 2, root.height / 2))

    private fun zoomInToCenter() = controller.zoomIn(DoublePoint(root.width / 2, root.height / 2))

    companion object {
        const val framesMargin = 10.0
        const val scrollSpeed = 20.0
    }
}
