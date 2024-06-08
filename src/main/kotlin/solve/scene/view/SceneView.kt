package solve.scene.view

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.scene.input.MouseEvent
import javafx.stage.Screen
import org.joml.Vector2i
import solve.rendering.canvas.SceneCanvas
import solve.rendering.engine.core.input.MouseButton
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import tornadofx.View
import tornadofx.onChange
import javafx.scene.input.MouseButton as JavaFXMouseButton

/**
 * Scene visual component, represents grid with frames.
 * Set up global scene properties.
 */
class SceneView : View() {
    private val controller: SceneController by inject()
    private val canvas = SceneCanvas()

    private var mouseScreenPoint = Vector2i()

    private var wasMouseDragging = false

    override val root = canvas.canvas

    private val sceneChangedEventHandler = InvalidationListener {
        if (SceneFacade.lastVisualizationKeepSettings) {
            canvas.setFramesSelection(controller.scene.frames)
        } else {
            canvas.setNewScene(controller.scene)
        }
    }

    init {
        addBindings()
    }

    private fun extrudeEventMousePosition(event: MouseEvent) : Vector2i {
        val screen = Screen.getPrimary()
        return Vector2i(
            (event.x * screen.outputScaleX).toInt(),
            (event.y * screen.outputScaleY).toInt()
        )
    }
    private fun addBindings() {
        addSceneParamsBindings()
        addSceneFramesBindings()
        addInputBindings()
    }

    private fun addSceneFramesBindings() {
        controller.sceneProperty.addListener(sceneChangedEventHandler)
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
            if (getMouseButton(event) != MouseInteractionButton) {
                return@setOnMouseDragged
            }
            wasMouseDragging = true
            mouseScreenPoint = extrudeEventMousePosition(event)
            canvas.dragTo(mouseScreenPoint)
        }
        root.setOnMousePressed { event ->
            if (getMouseButton(event) != MouseInteractionButton) {
                return@setOnMousePressed
            }
            canvas.startDragging(mouseScreenPoint)
        }
        root.setOnMouseReleased { event ->
            if (getMouseButton(event) != MouseInteractionButton) {
                return@setOnMouseReleased
            }
            canvas.stopDragging()
        }
        root.setOnMouseClicked { event ->
            val mouseButton = getMouseButton(event) ?: return@setOnMouseClicked

            if (!wasMouseDragging) {
                canvas.handleClick(mouseScreenPoint, mouseButton)
            }
            wasMouseDragging = false
        }
        root.setOnScroll { event ->
            val scrollDelta = event.deltaY
            if (scrollDelta == 0.0) {
                return@setOnScroll
            }
            if (scrollDelta > 0) {
                controller.increaseScale()
            } else {
                controller.decreaseScale()
            }
        }
    }

    private fun getMouseButton(event: MouseEvent): MouseButton? {
        return when (event.button) {
            JavaFXMouseButton.PRIMARY -> MouseButton.Left
            JavaFXMouseButton.MIDDLE -> MouseButton.Middle
            JavaFXMouseButton.SECONDARY -> MouseButton.Right
            else -> null
        }
    }

    companion object {
        private val MouseInteractionButton = MouseButton.Left

        const val framesMargin = 0.0
        const val scrollSpeed = 20.0
    }
}
