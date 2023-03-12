package solve.scene.view.drawing

import javafx.scene.image.Image
import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.scene.model.createPairs
import solve.utils.ceilToInt

class ImageFrameElement(viewOrder: Short, private val image: Image) : FrameElement(viewOrder) {
    override val points = createPairs(image.width.ceilToInt().toShort(), image.height.ceilToInt().toShort())
    override fun getColor(point: Point): Color = image.pixelReader.getColor(point.x.toInt(), point.y.toInt())
}