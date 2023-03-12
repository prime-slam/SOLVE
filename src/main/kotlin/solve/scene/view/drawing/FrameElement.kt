package solve.scene.view.drawing

import javafx.scene.paint.Color
import solve.scene.model.Point

abstract class FrameElement(initialViewOrder: Short) : Comparable<FrameElement> {
    var viewOrder = initialViewOrder
        protected set

    abstract val points: Iterable<Point>

    abstract fun getColor(point: Point): Color

    override fun compareTo(other: FrameElement): Int {
        if (other.viewOrder < viewOrder) {
            return -1
        }
        if (other.viewOrder > viewOrder) {
            return 1
        }
        return 0
    }
}