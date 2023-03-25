package solve.scene.view.drawing

import javafx.scene.image.Image
import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.scene.model.PointPairs
import solve.scene.model.Size
import solve.utils.ceilToInt

class ImageFrameElement(viewOrder: Int, private val image: Image) : FrameElement(viewOrder) {
    override val points = PointPairs.getPairs(Size(image.width.ceilToInt(), image.height.ceilToInt()))
    override fun getColor(point: Point): Color = image.pixelReader.getColor(point.x.toInt(), point.y.toInt())
}