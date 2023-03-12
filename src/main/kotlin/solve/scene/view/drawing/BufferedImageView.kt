package solve.scene.view.drawing

import javafx.scene.image.WritableImage
import javafx.scene.image.ImageView
import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelFormat
import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.utils.ceilToInt
import java.nio.IntBuffer

class BufferedImageView(
    private val width: Double, private val height: Double, initialScale: Double
) : ImageView() {
    data class Pixel(val color: Color, val point: Point)

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

    fun drawPixels(pixels: Iterable<Pixel>) {
        pixels.forEach { pixel -> this.pixels[pixel.point.x + roundedWidth * pixel.point.y] = pixel.color.toArgb() }
        updateCanvas()
    }

    private fun updateCanvas() {
        pixelBuffer.updateBuffer { null }
    }

    private fun Color.toArgb() =
        (opacity * 255).toInt() shl 24 or ((red * 255).toInt() shl 16) or ((green * 255).toInt() shl 8) or (blue * 255).toInt()
}