package sliv.tool.scene.view

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
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

    init {
        controller.scene.onChange { scene ->
            if (scene != null) {
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
        val frames = (0 until scene.framesCount).mapNotNull { i -> scene.getFrame.invoke(i) }

        //TODO: get size from the first image after data virtualization will be done
        val width = 500.0
        val height = 500.0
        val margin = 10.0

        val scaleProperty = SimpleDoubleProperty(1.0)

        val columnsNumber = 30 //TODO: should be set from the UI

        val grid = VirtualizedFXGridProvider.createGrid(
            frames, columnsNumber, width + margin, height + margin, scaleProperty
        ) { frame ->
            FrameView(
                width, height, scaleProperty, frame
            )
        }

        grid.setUpPanning()

        grid.setOnScroll { event ->
            if (event.isConsumed) { // If event is consumed by vsp
                return@setOnScroll
            }
            if(event.deltaY.compareTo(0) == 0) {
                return@setOnScroll
            }
            zoomGrid(scaleProperty, grid, event.x to event.y, event.deltaY > 0)
        }

        add(grid.node)
    }

    private fun zoomGrid(scaleProperty: DoubleProperty, grid: Grid, mousePos: Pair<Double, Double>, isPositive: Boolean) {
        val initialPos = grid.getPosition()

        val initialMouseX = (initialPos.first + mousePos.first) / scaleProperty.value
        val initialMouseY = (initialPos.second + mousePos.second) / scaleProperty.value

        if (isPositive) {
            scaleProperty.value = min(scaleProperty.value * scaleFactor, maxScale)
        } else {
            scaleProperty.value = max(scaleProperty.value / scaleFactor, minScale)
        }

        val translatedMouseX = initialMouseX * scaleProperty.value
        val translatedMouseY = initialMouseY * scaleProperty.value

        grid.scrollTo(translatedMouseX - mousePos.first, translatedMouseY - mousePos.second)
    }

    companion object {
        private const val scaleFactor = 1.05
        // In maximum scale pane renders 4 canvases. If size is too big, JavaFX rendering crashes.
        // To solve the problem, a user should set VM option -Dprism.order=sw
        // Now max scale is limited to avoid the issue.
        private const val maxScale = 8.0
        private const val minScale = 0.2
    }
}