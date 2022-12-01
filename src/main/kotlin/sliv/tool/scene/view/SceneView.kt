package sliv.tool.scene.view

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import sliv.tool.scene.controller.SceneController
import sliv.tool.scene.view.virtualizedfx.VirtualizedFXGridProvider
import tornadofx.View
import tornadofx.label
import tornadofx.onChange
import tornadofx.vbox
import kotlin.math.max
import kotlin.math.min

class SceneView : View() {
    private val controller: SceneController by inject()
    private var frameDataLoadingScope = CoroutineScope(Dispatchers.Default)

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
        root.children.clear()

        val scene = controller.scene.value

        if (scene.frames.isEmpty()) {
            label("No frames was provided")
            return
        }

        val firstImage = scene.frames.first().image
        val width = firstImage.width
        val height = firstImage.height
        val margin = 10.0

        val scaleProperty = SimpleDoubleProperty(1.0)

        val columnsNumber = 30 //TODO: should be set from the UI
        val emptyFrames = (0 until (columnsNumber - scene.frames.count() % columnsNumber)).map { null }
        val frames = scene.frames + emptyFrames

        val grid = VirtualizedFXGridProvider.createGrid(
            frames, columnsNumber, width + margin, height + margin, scaleProperty
        ) { frame ->
            FrameView(
                width, height, scaleProperty, frameDataLoadingScope, frame
            )
        }

        grid.setUpPanning()

        grid.setOnScroll { event ->
            if (event.isConsumed) { // If event is consumed by vsp
                return@setOnScroll
            }
            if (event.deltaY == 0.0) {
                return@setOnScroll
            }
            zoomGrid(scaleProperty, grid, event.x to event.y, event.deltaY > 0)
        }

        add(grid.node)
    }

    private fun zoomGrid(
        scaleProperty: DoubleProperty, grid: Grid, mousePosition: Pair<Double, Double>, isPositive: Boolean
    ) {
        val initialPos = grid.currentPosition

        val initialMouseX = (initialPos.first + mousePosition.first) / scaleProperty.value
        val initialMouseY = (initialPos.second + mousePosition.second) / scaleProperty.value

        if (isPositive) {
            scaleProperty.value = min(scaleProperty.value * scaleFactor, maxScale)
        } else {
            scaleProperty.value = max(scaleProperty.value / scaleFactor, minScale)
        }

        val translatedMouseX = initialMouseX * scaleProperty.value
        val translatedMouseY = initialMouseY * scaleProperty.value

        grid.scrollTo(translatedMouseX - mousePosition.first, translatedMouseY - mousePosition.second)
    }

    companion object {
        private const val scaleFactor = 1.05

        private const val maxScale = 20.0
        private const val minScale = 0.2
    }
}