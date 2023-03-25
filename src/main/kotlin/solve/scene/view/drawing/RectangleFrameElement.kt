import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.scene.model.PointPairs
import solve.scene.model.Size
import solve.scene.view.drawing.FrameElement

class RectangleFrameElement(viewOrder: Int, private val color: Color, width: Int, height: Int) : FrameElement(viewOrder) {
    override val points = PointPairs.getPairs(Size(width, height))
    override fun getColor(point: Point) = color
}