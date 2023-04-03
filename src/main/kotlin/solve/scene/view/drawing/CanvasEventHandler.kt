package solve.scene.view.drawing

import javafx.event.Event
import javafx.event.EventHandler

data class CanvasEventHandler<T : Event>(
    val frameElement: FrameElement, val handler: EventHandler<T>
) : Comparable<CanvasEventHandler<T>> {
    override fun compareTo(other: CanvasEventHandler<T>): Int {
        val viewOrderComparison = other.frameElement.viewOrder.compareTo(frameElement.viewOrder)
        if (viewOrderComparison != 0) {
            return viewOrderComparison
        }
        return other.frameElement.hashCode().compareTo(frameElement.hashCode())
    }
}