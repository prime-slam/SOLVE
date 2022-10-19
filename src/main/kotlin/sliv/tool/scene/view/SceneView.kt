package sliv.tool.scene.view

import sliv.tool.scene.controller.SceneController
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

    //TODO: some virtualized pane to place frames
    override val root = vbox {
        label("Empty scene placeholder")
    }

    private fun draw() {
        root.children.clear()
        root.add(vbox {
            val scene = controller.scene.value
            val frames = (0 until scene.framesCount).map { i -> scene.getFrame.invoke(i)!! }
            scrollpane {
                gridpane {
                    frames.forEachIndexed { index, frame ->
                        group {
                            add(FrameView(600.0, 600.0, frame))
                        }.gridpaneConstraints {
                            columnRowIndex(0, index)
                        }
                    }
                }
            }
        })
    }
}
