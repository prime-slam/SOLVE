package sliv.tool.scene.view

import javafx.beans.property.SimpleDoubleProperty
import sliv.tool.scene.controller.SceneController
import sliv.tool.scene.view.virtualizedfx.VirtualizedFXGridProvider
import tornadofx.*

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

        val scaleFactor = 0.05
        grid.getNode().setOnScroll { event ->
            if(event.deltaY > 0) {
                scaleProperty.value += scaleFactor
            } else if(event.deltaY < 0) {
                scaleProperty.value -= scaleFactor
            }
        }

        add(grid.getNode())
    }
}