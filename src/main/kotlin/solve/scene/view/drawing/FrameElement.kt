package solve.scene.view.drawing

import javafx.scene.paint.Color
import solve.scene.model.Point

// Item which can be drawn with FrameDrawer
abstract class FrameElement(initialViewOrder: Int) {
    // Determines z-order of elements drawn with FrameDrawer
    // Landmark with greater viewOrder will be drawn above
    // If there are two FrameElements containing the same point with equal viewOrder,
    // behavior is not specified
    var viewOrder = initialViewOrder
        protected set

    // Points occupied by the FrameElement
    abstract val points: List<Point>

    // Returns the color of the shape at the point
    // Note that it can return value even if point is not contained in FrameElement
    abstract fun getColor(point: Point): Color
}
