package solve.scene.view

import javafx.scene.canvas.Canvas
import javafx.scene.image.*
import javafx.scene.paint.Color
import javafx.scene.transform.Scale
import solve.scene.model.Point
import kotlin.math.ceil

class BufferedCanvas(
    initialWidth: Double,
    initialHeight: Double,
    initialScale: Double,
    private val buffer: IntArray
) :
    Canvas(initialWidth, initialHeight) {
    var scale: Double = initialScale
        set(value) {
            field = value
            transforms.clear()
            transforms.add(Scale(scale, scale))
        }

    private val pixelFormat = PixelFormat.getIntArgbPreInstance()

    private val roundedWidth: Int get() = ceil(width).toInt()
    private val roundedHeight: Int get() = ceil(height).toInt()

    fun drawImage(image: Image) {
        val imagePixelReader = image.pixelReader
        imagePixelReader.getPixels(0, 0, roundedWidth, roundedHeight, pixelFormat, buffer, 0, roundedWidth)
        updateCanvas()
    }

    fun fill(color: Color) {
        val argbColor = color.toArgb()
        for (i in buffer.indices) {
            buffer[i] = argbColor
        }
        updateCanvas()
    }

    fun drawPoints(color: Color, points: Iterable<Point>) {
        val argbColor = color.toArgb()
        val pixelFormat = PixelFormat.getIntArgbPreInstance()
        points.forEach { point ->
            buffer[point.x + roundedWidth * point.y] = argbColor
        }
        val pw = graphicsContext2D.pixelWriter
        pw.setPixels(0, 0, roundedWidth, roundedHeight, pixelFormat, buffer, 0, roundedWidth)
        updateCanvas()
    }

    fun clear() {
        for (i in buffer.indices) {
            buffer[i] = 0
        }
        updateCanvas()
    }

    private fun updateCanvas() {
        val pw = graphicsContext2D.pixelWriter
        pw.setPixels(0, 0, roundedWidth, roundedHeight, pixelFormat, buffer, 0, roundedWidth)
    }

    private fun Color.toArgb(): Int {
        return 255 shl 24 or
                ((red * 255).toInt() shl 16) or
                ((green * 255).toInt() shl 8) or
                (blue * 255).toInt()
    }
}