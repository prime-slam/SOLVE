package solve.scene.view.drawing

import javafx.scene.paint.Color
import solve.scene.model.Point

/**
 * Visual element of frame, which can be drawn using FrameDrawer.
 */
abstract class FrameElement(initialViewOrder: Int) {
    /**
     * Determines z-order of elements within canvas.
     * Element with greater viewOrder will be drawn above.
     * If there are two FrameElements containing the same point with equal viewOrder,
     * behavior is not specified
     */
    var viewOrder = initialViewOrder
        protected set

    /**
     * Points occupied by the element.
     */
    abstract val points: List<Point>

    /**
     * Returns the color of the element at the point.
     * Note that it can return value even if point is not contained in FrameElement.
     */
    abstract fun getColor(point: Point): Color
}
