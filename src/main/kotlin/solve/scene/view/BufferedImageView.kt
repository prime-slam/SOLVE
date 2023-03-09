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
    private var drawnImage: Image? = null

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
        drawnImage = image
        val imagePixelReader = image.pixelReader
        imagePixelReader.getPixels(0, 0, roundedWidth, roundedHeight, pixelFormat, pixels, 0, roundedWidth)
        updateCanvas()
    }

    fun fill(color: Color) {
        for (i in pixels.indices) {
            overlayPixel(i, color)
        }
        updateCanvas()
    }

    fun drawPoints(color: Color, points: Iterable<Point>) {
        points.forEach { point ->
            overlayPixel(point.x + roundedWidth * point.y, color)
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

    private fun overlayPixel(i: Int, color: Color) {
        val x = i % roundedWidth
        val y = i / roundedWidth
        val pixelColor = drawnImage?.pixelReader?.getColor(x, y)
        val overlayColor = if (pixelColor != null) overlayColors(color, pixelColor) else color
        pixels[i] = overlayColor.toArgb()
    }

    // Calculates color of a pixel after color1 overlays color2, see alpha compositing
    private fun overlayColors(color1: Color, color2: Color): Color {
        val opacity = color1.opacity + color2.opacity * (1 - color1.opacity)
        val red = overlayComponents(color1.red, color2.red, color1.opacity, color2.opacity, opacity)
        val green = overlayComponents(color1.green, color2.green, color1.opacity, color2.opacity, opacity)
        val blue = overlayComponents(color1.blue, color2.blue, color1.opacity, color2.opacity, opacity)
        return Color(red, green, blue, opacity)
    }

    // Calculates one component (red, green, blue) of color during overlaying
    private fun overlayComponents(
        component1: Double, component2: Double, opacity1: Double, opacity2: Double, opacity: Double
    ) = (component1 * opacity1 + (1 - opacity1) * component2 * opacity2) / opacity

    // Converts color specified as integer in argb format into a JavaFX color
    private fun Int.toColor(): Color {
        val opacity = ((this shr 24) and 0xff) / 255.0
        val red = ((this shr 16) and 0xff) / 255.0
        val green = ((this shr 8) and 0xff) / 255.0
        val blue = (this and 0xff) / 255.0
        return Color(red, green, blue, opacity)
    }

    private fun Color.toArgb() =
        (opacity * 255).toInt() shl 24 or ((red * 255).toInt() shl 16) or ((green * 255).toInt() shl 8) or (blue * 255).toInt()
}