package solve.scene.view

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
import kotlin.math.min

class SceneView : View() {
    private val controller: SceneController by inject()
    private var frameDataLoadingScope = CoroutineScope(Dispatchers.Default)
    private var currentGrid: Grid? = null

    init {
        controller.scene.onChange { scene ->
            if (scene != null) {
                frameDataLoadingScope.cancel()
                frameDataLoadingScope = CoroutineScope(Dispatchers.Default)
                draw()
            }
        }
    }

    override val root = vbox {
        label("Empty scene placeholder")
    }

    private fun draw() {
        currentGrid?.xProperty?.unbindBidirectional(controller.xProperty)
        currentGrid?.yProperty?.unbindBidirectional(controller.yProperty)
        currentGrid?.dispose()
        root.children.clear()

        val scene = controller.scene.value

        if (scene.frames.isEmpty()) {
            label("No frames was provided")
            return
        }

        val firstImage = scene.frames.first().getImage()
        val width = firstImage.width
        val height = firstImage.height
        val margin = 10.0

        val columnsNumber = min(scene.frames.size, defaultNumberOfColumns) //TODO: should be set from the UI
        // VirtualizedFX Grid assumes that frames count is a divider for the columns number
        val emptyFrames = (0 until (columnsNumber - scene.frames.count() % columnsNumber) % columnsNumber).map { null }
        val frames = scene.frames + emptyFrames

        val outOfFramesLayer = OutOfFramesLayer()
        val associationsManager = AssociationsManager(width, height, margin, controller.scaleProperty, scene.frames, columnsNumber, outOfFramesLayer)
        val grid = VirtualizedFXGridProvider.createGrid(
            frames, columnsNumber, width + margin, height + margin, controller.scaleProperty, outOfFramesLayer
        ) { frame ->
            FrameView(
                width, height, controller.scaleProperty, frameDataLoadingScope, associationsManager, scene, frame
            )
        }

        grid.setUpPanning()

        grid.xProperty.bindBidirectional(controller.xProperty)
        grid.yProperty.bindBidirectional(controller.yProperty)

        grid.setOnScroll { event ->
            if (event.isConsumed) { // If event is consumed by vsp
                return@setOnScroll
            }
            if (event.deltaY == 0.0) {
                return@setOnScroll
            }

            val mousePosition = event.x to event.y
            if (event.deltaY > 0) {
                controller.zoomIn(mousePosition)
            } else {
                controller.zoomOut(mousePosition)
            }
        }

        currentGrid = grid
        add(grid.node)
    }

    companion object {
        private const val defaultNumberOfColumns = 15
    }
}