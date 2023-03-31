package solve.scene.view.drawing

import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.input.MouseEvent

class FrameEventManager(canvasNode: Node, private val scaleProperty: DoubleProperty) {
    private val mousePressedHandlers = sortedSetOf<CanvasEventHandler<MouseEvent>>()
    private val mouseReleasedHandlers = sortedSetOf<CanvasEventHandler<MouseEvent>>()

    init {
        canvasNode.addEventHandler(MouseEvent.MOUSE_PRESSED) { mouse ->
            invokeMouseEventHandlers(mousePressedHandlers, mouse)
        }
        canvasNode.addEventHandler(MouseEvent.MOUSE_RELEASED) { mouse ->
            invokeMouseEventHandlers(mouseReleasedHandlers, mouse)
        }
    }

    fun subscribeMousePressed(eventHandler: CanvasEventHandler<MouseEvent>) {
        mousePressedHandlers.add(eventHandler)
    }

    fun unsubscribeMousePressed(eventHandler: CanvasEventHandler<MouseEvent>) {
        mousePressedHandlers.remove(eventHandler)
    }

    fun subscribeMouseReleased(eventHandler: CanvasEventHandler<MouseEvent>) {
        mouseReleasedHandlers.add(eventHandler)
    }

    fun unsubscribeMouseReleased(eventHandler: CanvasEventHandler<MouseEvent>) {
        mouseReleasedHandlers.remove(eventHandler)
    }

    private fun invokeMouseEventHandlers(handlers: Iterable<CanvasEventHandler<MouseEvent>>, mouse: MouseEvent) {
        for (handler in handlers) {
            if (mouse.isConsumed) {
                return
            }
            if (isMouseOver(handler.frameElement, mouse)) {
                handler.handler.handle(mouse)
            }
        }
    }

    private fun isMouseOver(frameElement: FrameElement, mouse: MouseEvent): Boolean {
        val scale = scaleProperty.value
        val mouseX = (mouse.x / scale).toInt().toShort()
        val mouseY = (mouse.y / scale).toInt().toShort()
        return frameElement.points.any { point -> point.x == mouseX && point.y == mouseY }
    }
}