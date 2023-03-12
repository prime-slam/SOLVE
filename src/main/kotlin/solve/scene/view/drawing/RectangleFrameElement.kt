package solve.scene.view.drawing

import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.scene.model.createPairs

class RectangleFrameElement(viewOrder: Short, private val color: Color, width: Short, height: Short) : FrameElement(viewOrder) {
    override val points = createPairs(width, height)
    override fun getColor(point: Point) = color
}