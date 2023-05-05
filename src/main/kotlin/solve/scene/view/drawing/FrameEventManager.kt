package solve.scene.view.drawing

import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.input.MouseEvent

/**
 * Responds for routing frame mouse events to frame elements.
 * Calls subscriber from the frame element which has point of the event.
 * If there are a few overlapping frame elements at the point, the topmost gets the event.
 */
class FrameEventManager(canvasNode: Node, private val scaleProperty: DoubleProperty) {
    private val mousePressedHandlers = mutableSetOf<CanvasEventHandler<MouseEvent>>()
    private val mouseReleasedHandlers = mutableSetOf<CanvasEventHandler<MouseEvent>>()

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

    /**
     * Sorts handlers by view order and calls first appropriate handler.
     */
    private fun invokeMouseEventHandlers(handlers: Iterable<CanvasEventHandler<MouseEvent>>, mouse: MouseEvent) {
        for (handler in handlers.sorted()) {
            if (isMouseOver(handler.frameElement, mouse)) {
                handler.handler.handle(mouse)
                return
            }
        }
    }

    /**
     * Checks if the frame element occupy the point.
     */
    private fun isMouseOver(frameElement: FrameElement, mouse: MouseEvent): Boolean {
        val scale = scaleProperty.value
        val mouseX = (mouse.x / scale).toInt().toShort()
        val mouseY = (mouse.y / scale).toInt().toShort()
        return frameElement.points.any { point -> point.x == mouseX && point.y == mouseY }
    }
}
