package solve.scene.view

import javafx.application.Platform
import solve.rendering.canvas.SceneCanvas
import solve.scene.controller.SceneController
import solve.scene.model.Landmark
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.AssociationsManager
import tornadofx.View
import tornadofx.onChange

/**
 * Scene visual component, represents grid with frames.
 * Set up global scene properties.
 */
class SceneView : View() {
    private val controller: SceneController by inject()
    private val canvas = SceneCanvas()

    var currentAssociationsManager: AssociationsManager<VisualizationFrame, Landmark.Keypoint>? = null
        private set

    override val root = canvas.canvas

    init {
        addBindings()
    }

    private fun setUpScrollingOnMouseWheel() { } // TODO

    private fun bindPositionProperties() { } // TODO
    private fun unbindPositionProperties() {
        controller.xProperty.unbind()
        controller.yProperty.unbind()
        controller.scrollX = null
        controller.scrollY = null
    }

    private fun addBindings() {
        controller.sceneWidthProperty.bind(root.widthProperty())

        // Grid settings bindings.
        controller.installedColumnsNumberProperty.onChange {
            // TODO
        }
        Platform.runLater {
            if (this.root.scene != null) {
                currentWindow?.widthProperty()?.onChange {
                    Platform.runLater {
                        controller.recalculateScale(true)
                    }
                }
            }
        }
    }

    private fun scrollRight() { } // TODO

    private fun scrollLeft() { } // TODO

    private fun scrollUp() { } // TODO

    private fun scrollDown() { } // TODO
    private fun zoomOutFromCenter() { } // TODO

    private fun zoomInToCenter() { } // TODO

    companion object {
        const val framesMargin = 10.0
        const val scrollSpeed = 20.0
    }
}
