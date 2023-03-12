package solve.scene.view.drawing

import javafx.scene.image.WritableImage
import javafx.scene.image.ImageView
import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelFormat
import solve.scene.model.Point
import solve.utils.ceilToInt
import java.nio.IntBuffer

class BufferedImageView(
    private val width: Double, private val height: Double, initialScale: Double
) : ImageView() {
    private val buffer = IntBuffer.allocate(width.ceilToInt() * height.ceilToInt())
    private val pixelFormat = PixelFormat.getIntArgbPreInstance()
    private val pixelBuffer = PixelBuffer(width.ceilToInt(), height.ceilToInt(), buffer, pixelFormat)
    private val pixels = buffer.array()
    private val writableImage = WritableImage(pixelBuffer)

    init {
        this.image = writableImage
        scale(initialScale)
    }

    fun scale(scale: Double) {
        this.fitWidth = width * scale
        this.fitHeight = height * scale
    }

    val roundedWidth: Int get() = writableImage.width.ceilToInt()
    val roundedHeight: Int get() = writableImage.height.ceilToInt()

    fun setPixelValue(point: Point, color: Int) {
        this.pixels[point.x + roundedWidth * point.y] = color
    }

    fun redraw() {
        pixelBuffer.updateBuffer { null }
    }
}