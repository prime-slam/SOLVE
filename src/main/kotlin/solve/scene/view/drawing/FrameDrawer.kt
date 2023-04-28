package solve.scene.view.drawing

import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.scene.model.PointPairs
import solve.scene.model.Size
import solve.utils.getScreenPosition

class FrameDrawer(private val canvas: BufferedImageView, private val layersCount: Int) {
    val screenPosition
        get() = canvas.getScreenPosition()
    val width = canvas.roundedWidth
    val height = canvas.roundedHeight

    private val buffer = Array(width * height) { Array(layersCount) { 0 } }
    private val allPairs = PointPairs.getPairs(Size(width, height))

    fun clear() {
        allPairs.parallelStream().forEach { point ->
            val pointBuffer = buffer[point.x + width * point.y]
            for (i in pointBuffer.indices) {
                pointBuffer[i] = 0
            }
        }
    }

    // Updates colors of the points at the element layer
    // Previous values will be overwritten
    fun addOrUpdateElement(element: FrameElement) {
        element.points.parallelStream().forEach { point ->
            buffer[point.x + width * point.y][element.viewOrder] = element.getColor(point).toArgb()
        }
    }

    fun fullRedraw() {
        redrawPoints(allPairs)
    }

    fun redrawPoints(points: List<Point>) {
        points.parallelStream().forEach { point ->
            canvas.setPixelValue(point, getPixelColor(point))
        }
        canvas.redraw()
    }

    private fun getPixelColor(point: Point): Int {
        val pixelBuffer = buffer[point.x + width * point.y]
        var color = 0
        for (i in pixelBuffer.indices) {
            color = overlayColors(pixelBuffer[i], color)
        }
        return color
    }

    // Calculates color of a pixel after color1 overlays color2, see alpha compositing
    private fun overlayColors(color1: Int, color2: Int): Int {
        val opacity1 = color1.getOpacity()
        val opacity2 = color2.getOpacity()
        val opacity = opacity1 + opacity2 * (1 - opacity1)

        val red = overlayComponents(color1.getRed(), color2.getRed(), opacity1, opacity2, opacity)
        val green = overlayComponents(color1.getGreen(), color2.getGreen(), opacity1, opacity2, opacity)

        val blue1 = color1.getBlue()
        val blue2 = color2.getBlue()
        val blue = overlayComponents(blue1, blue2, opacity1, opacity2, opacity)

        return (opacity * 255).toInt() shl 24 or ((red * 255).toInt() shl 16) or ((green * 255).toInt() shl 8) or (blue * 255).toInt()
    }

    // Calculates one component (red, green, blue) of color during overlaying
    private fun overlayComponents(
        component1: Double,
        component2: Double,
        opacity1: Double,
        opacity2: Double,
        opacity: Double
    ) = (component1 * opacity1 + (1 - opacity1) * component2 * opacity2) / opacity

    private fun Color.toArgb() =
        (opacity * 255).toInt() shl 24 or ((red * 255).toInt() shl 16) or ((green * 255).toInt() shl 8) or (blue * 255).toInt()

    private fun Int.getOpacity() = ((this shr 24) and 0xff) / 255.0
    private fun Int.getRed() = ((this shr 16) and 0xff) / 255.0
    private fun Int.getGreen() = ((this shr 8) and 0xff) / 255.0
    private fun Int.getBlue() = (this and 0xff) / 255.0

    companion object {
        const val IMAGE_VIEW_ORDER = 0
        const val LANDMARKS_VIEW_ORDER = 1
    }
}
