package sliv.tool.scene.view

import javafx.beans.property.SimpleDoubleProperty
import sliv.tool.scene.controller.SceneController
import sliv.tool.scene.view.virtualizedfx.VirtualizedFXGridProvider
import tornadofx.*
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

        val scaleFactor = 1.05
        val minScale = 0.2

        // In maximum scale pane renders 4 canvases. If size is too big, JavaFX rendering crashes.
        // To solve the problem, a user should set VM option -Dprism.order=sw
        val maxScale = 15.0

        grid.setOnScroll { event ->
            if(event.isConsumed) { // If event is consumed by vsp
                return@setOnScroll
            }

            val initialPos = grid.getPosition()

            val initialMouseX = (initialPos.first + event.x) / scaleProperty.value
            val initialMouseY = (initialPos.second + event.y) / scaleProperty.value

            if(event.deltaY > 0) {
                scaleProperty.value = min(scaleProperty.value * scaleFactor, maxScale)
            } else if(event.deltaY < 0) {
                scaleProperty.value = max(scaleProperty.value / scaleFactor, minScale)
            }

            val translatedMouseX = initialMouseX * scaleProperty.value
            val translatedMouseY = initialMouseY * scaleProperty.value

            grid.scrollTo(translatedMouseX - event.x, translatedMouseY - event.y)
        }

        add(grid.getNode())
    }
}