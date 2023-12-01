package solve.scene.view

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.joml.Vector2i
import solve.rendering.canvas.SceneCanvas
import solve.scene.SceneFacade
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

    private var mouseScreenPoint = Vector2i()

    var currentAssociationsManager: AssociationsManager<VisualizationFrame, Landmark.Keypoint>? = null
        private set

    override val root = canvas.canvas

    private val projectChangedEventHandler = InvalidationListener {
        val framesSize = controller.scene.frameSize
        val framesSizeVector = Vector2i(framesSize.width.toInt(), framesSize.height.toInt())
        canvas.setNewSceneFrames(controller.scene.frames, framesSizeVector)
    }
    private val framesChangedEventHandler = InvalidationListener {
        canvas.setFramesSelection(controller.scene.frames)
    }

    init {
        addBindings()
    }

    private fun extrudeEventMousePosition(event: MouseEvent) = Vector2i(event.x.toInt(), event.y.toInt())

    private fun addBindings() {
        addSceneParamsBindings()
        addSceneFramesBindings()
        addInputBindings()
    }

    private fun addSceneFramesBindings() {
        SceneFacade.lastVisualizationKeepSettingsProperty.addListener(projectChangedEventHandler)
        controller.sceneProperty.addListener(framesChangedEventHandler)
    }

    private fun addSceneParamsBindings() {
        controller.sceneWidthProperty.bind(root.widthProperty())

        // Grid settings bindings.
        controller.installedColumnsNumberProperty.onChange { columnsNumber ->
            canvas.setColumnsNumber(columnsNumber)
        }
        controller.scaleProperty.onChange { scale ->
            canvas.zoomToPoint(mouseScreenPoint, scale.toFloat())
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

    private fun addInputBindings() {
        root.setOnMouseMoved { event ->
            mouseScreenPoint = extrudeEventMousePosition(event)
        }
        root.setOnMouseDragged { event ->
            if (event.button != MouseDragButton) {
                return@setOnMouseDragged
            }
            mouseScreenPoint = extrudeEventMousePosition(event)
            canvas.dragTo(mouseScreenPoint)
        }
        root.setOnMousePressed { event ->
            if (event.button != MouseDragButton) {
                return@setOnMousePressed
            }
            canvas.startDragging(mouseScreenPoint)
        }
        root.setOnMouseReleased { event ->
            if (event.button != MouseDragButton) {
                return@setOnMouseReleased
            }
            canvas.stopDragging()
        }
        root.setOnScroll { event ->
            val scrollDelta = event.deltaY
            if (scrollDelta > 0) {
                controller.increaseScale()
            } else {
                controller.decreaseScale()
            }
        }
    }

    companion object {
        private val MouseDragButton = MouseButton.MIDDLE

        const val framesMargin = 0.0
        const val scrollSpeed = 20.0
    }
}
