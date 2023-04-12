package solve.scene.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import solve.scene.controller.SceneController
import solve.scene.view.association.AssociationsManager
import solve.scene.view.association.OutOfFramesLayer
import solve.scene.view.virtualizedfx.VirtualizedFXGridProvider
import solve.utils.structures.DoublePoint as DoublePoint
import solve.utils.structures.Size as DoubleSize
import tornadofx.View
import tornadofx.label
import tornadofx.onChange
import tornadofx.vbox

class SceneView : View() {
    private val controller: SceneController by inject()
    private var frameDataLoadingScope = CoroutineScope(Dispatchers.Default)
    private var currentGrid: Grid? = null
    private var frameViewCache: FrameViewCache? = null

    override val root = vbox {
        label("Empty scene placeholder")
    }

    init {
        controller.sceneWidthProperty.bind(root.widthProperty())
        controller.sceneProperty.onChange { scene ->
            if (scene != null) {
                frameDataLoadingScope.cancel()
                frameDataLoadingScope = CoroutineScope(Dispatchers.Default)
                draw()
            }
        }
    }

    private fun draw() {
        unbindPositionProperties()
        currentGrid?.dispose()
        root.children.clear()

        val scene = controller.sceneProperty.value

        if (scene.frames.isEmpty()) {
            label("No frames was provided")
            return
        }

        val frameSize = scene.frameSize
        val gridCellSize = DoubleSize(frameSize.width + framesMargin, frameSize.height + framesMargin)

        val columnsNumber = controller.columnsCount
        // VirtualizedFX Grid assumes that frames count is a divider for the columns number
        val emptyFrames = (0 until (columnsNumber - scene.frames.count() % columnsNumber) % columnsNumber).map { null }
        val frames = scene.frames + emptyFrames

        val outOfFramesLayer = OutOfFramesLayer()
        val associationsManager = AssociationsManager(
            frameSize, framesMargin, controller.scaleProperty, scene.frames, columnsNumber, outOfFramesLayer
        )

        val frameViewParameters = FrameViewParameters(frameDataLoadingScope, associationsManager, scene)

        val canReuseCache =
            frameViewCache?.size == frameSize && frameViewCache?.canvasBufferDepth == scene.canvasLayersCount
        frameViewCache = if (canReuseCache) frameViewCache!! else FrameViewCache(
            frameSize, scene.canvasLayersCount
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
        val grid = VirtualizedFXGridProvider.createGrid(
            frames, columnsNumber, gridCellSize, controller.scaleProperty, outOfFramesLayer
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

    companion object {
        const val framesMargin = 10.0
    }
}
