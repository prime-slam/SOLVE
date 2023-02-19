package solve.scene.view

import javafx.scene.image.*
import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.utils.ceilToInt
import java.nio.IntBuffer

class BufferedImageView(
    private val initialWidth: Double, private val initialHeight: Double, initialScale: Double
) : ImageView() {
    private val buffer = IntBuffer.allocate(initialWidth.ceilToInt() * initialHeight.ceilToInt())
    private val pixelFormat = PixelFormat.getIntArgbPreInstance()
    private val pixelBuffer = PixelBuffer(initialWidth.ceilToInt(), initialHeight.ceilToInt(), buffer, pixelFormat)
    private val pixels = buffer.array()
    private val writableImage = WritableImage(pixelBuffer)

    init {
        this.image = writableImage
        scale(initialScale)
    }

    fun scale(scale: Double) {
        this.fitWidth = initialWidth * scale
        this.fitHeight = initialHeight * scale
    }

    private val roundedWidth: Int get() = writableImage.width.ceilToInt()
    private val roundedHeight: Int get() = writableImage.height.ceilToInt()

    fun drawImage(image: Image) {
        val imagePixelReader = image.pixelReader
        imagePixelReader.getPixels(0, 0, roundedWidth, roundedHeight, pixelFormat, pixels, 0, roundedWidth)
        updateCanvas()
    }

    fun fill(color: Color) {
        val argbColor = color.toArgb()
        for (i in pixels.indices) {
            pixels[i] = argbColor
        }
        updateCanvas()
    }

    fun drawPoints(color: Color, points: Iterable<Point>) {
        val argbColor = color.toArgb()
        points.forEach { point ->
            pixels[point.x + roundedWidth * point.y] = argbColor
        }
        updateCanvas()
    }

    fun clear() {
        for (i in pixels.indices) {
            pixels[i] = 0
        }
        updateCanvas()
    }

    private fun updateCanvas() {
        pixelBuffer.updateBuffer { null }
    }

    private fun Color.toArgb() =
        255 shl 24 or ((red * 255).toInt() shl 16) or ((green * 255).toInt() shl 8) or (blue * 255).toInt()
}