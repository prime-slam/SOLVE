package sliv.tool.scene.view

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
        val frames = (0 until scene.framesCount).map { i -> scene.getFrame.invoke(i)!! }

        //TODO: all cells should have the same size. I can get it from the image of the first frame. What to do if some images have another size (irregular case).
        val width = 500.0
        val height = 500.0

        val margin = 10.0

        val columnsNumber = 30 //TODO: should be set from the UI

        val grid =
            VirtualizedFXGridProvider.createGrid(frames, columnsNumber, width + margin, height + margin) { frame ->
                FrameView(
                    width,
                    height,
                    frame
                )
            }

        add(grid)
    }
}
