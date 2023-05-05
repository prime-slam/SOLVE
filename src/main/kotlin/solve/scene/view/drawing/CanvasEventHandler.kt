package solve.scene.view.drawing

import javafx.event.Event
import javafx.event.EventHandler

/**
 * Aggregates event handler function with the corresponding frame element.
 * Implements Comparable interface to allow sorting by view order.
 * @see FrameEventManager
 */
data class CanvasEventHandler<T : Event>(
    val frameElement: FrameElement,
    val handler: EventHandler<T>
) : Comparable<CanvasEventHandler<T>> {
    override fun compareTo(other: CanvasEventHandler<T>): Int {
        return other.frameElement.viewOrder.compareTo(frameElement.viewOrder)
    }
}
