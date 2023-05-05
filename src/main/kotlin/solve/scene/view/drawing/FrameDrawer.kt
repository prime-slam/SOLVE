package solve.scene.view.drawing

import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.scene.model.PointPairs
import solve.scene.model.Size
import solve.utils.getScreenPosition

/**
 * High-level canvas drawing manager.
 * Allows to add abstract canvas elements such as bitmasks, images, rectangles, etc.
 * To create new element of frame, create an inheritor of FrameElement,
 * specify points of the element and colors in points.
 *
 * @param canvas visual image node, which draws calculated pixels.
 * @param layersCount overlapping layers count, addition of element with viewOrder
 * which is not in bounds of layers count causes ArrayIndexOutOfBoundsException
 */
class FrameDrawer(private val canvas: BufferedImageView, private val layersCount: Int) {
    /**
     * Actual bounds within the visual parent
     */
    val screenPosition
        get() = canvas.getScreenPosition()
    val width = canvas.roundedWidth
    val height = canvas.roundedHeight

    /**
     * Pixels colors 3D-matrix, the position of array of layers colors in the specified point (x, y)
     * is x + width * y
     */
    private val buffer = Array(width * height) { Array(layersCount) { 0 } }
    private val allPairs = PointPairs.getPairs(Size(width, height))

    /**
     * Drops all pixels in all layers to default value of #00000000.
     * Note that this function doesn't provoke redrawing, call it manually.
     */
    fun clear() {
        allPairs.parallelStream().forEach { point ->
            val pointBuffer = buffer[point.x + width * point.y]
            for (i in pointBuffer.indices) {
                pointBuffer[i] = 0
            }
        }
    }

    /**
     * Updates color of the points at the element layer in the buffer.
     * All previous values it its points will be overwritten.
     * Note that this function doesn't provoke redrawing, call it manually.
     */
    fun addOrUpdateElement(element: FrameElement) {
        element.points.parallelStream().forEach { point ->
            buffer[point.x + width * point.y][element.viewOrder] = element.getColor(point).toArgb()
        }
    }

    /**
     * Redraws all points within canvas.
     */
    fun fullRedraw() {
        redrawPoints(allPairs)
    }

    /**
     * Recalculates values of pixels in the specified points,
     * refreshes BufferedImageView to update visible image.
     */
    fun redrawPoints(points: List<Point>) {
        points.parallelStream().forEach { point ->
            canvas.setPixelValue(point, getPixelColor(point))
        }
        canvas.redraw()
    }

    /**
     * Calculates color of a pixel in the specified point.
     * Overlays the colors of the layers one by one.
     * The overlay order is determined by the viewOrder value.
     * ViewOrder = 0 stands for the lowest layer,
     * layer with bigger viewOrder will be above than layer with smaller.
     */
    private fun getPixelColor(point: Point): Int {
        val pixelBuffer = buffer[point.x + width * point.y]
        var color = 0
        for (i in pixelBuffer.indices) {
            color = overlayColors(pixelBuffer[i], color)
        }
        return color
    }

    /**
     * Calculates color of the pixel after color1 overlays color2 due to alpha compositing rules.
     * See https://ciechanow.ski/alpha-compositing/
     *
     * @param color1 color above.
     * @param color2 color below.
     */
    private fun overlayColors(color1: Int, color2: Int): Int {
        val opacity1 = color1.getOpacity()
        val opacity2 = color2.getOpacity()
        val opacity = opacity1 + opacity2 * (1 - opacity1)

        val red = overlayComponents(color1.getRed(), color2.getRed(), opacity1, opacity2, opacity)
        val green = overlayComponents(color1.getGreen(), color2.getGreen(), opacity1, opacity2, opacity)
        val blue = overlayComponents(color1.getBlue(), color2.getBlue(), opacity1, opacity2, opacity)

        return (opacity * 255).toInt() shl 24 or ((red * 255).toInt() shl 16) or ((green * 255).toInt() shl 8) or
            (blue * 255).toInt()
    }

    /**
     * Calculates one component (red, green, blue) of the color during overlaying
     *
     * @param component1 the value for component in the color above
     * @param component2 the value for component in the color below
     * @param opacity1 the value for opacity in the color above
     * @param opacity2 the value for opacity in the color below
     * @param opacity the value for opacity in the target color
     */
    private fun overlayComponents(
        component1: Double,
        component2: Double,
        opacity1: Double,
        opacity2: Double,
        opacity: Double
    ) = (component1 * opacity1 + (1 - opacity1) * component2 * opacity2) / opacity

    /**
     * Stores color information in an integer using argb format
     * | 8 bit opacity 0..255 | 8 bit red 0..255 | 8 bit green 0..255 | 8 bit blue 0..255 |
     */
    private fun Color.toArgb() = (opacity * 255).toInt() shl 24 or ((red * 255).toInt() shl 16) or
        ((green * 255).toInt() shl 8) or (blue * 255).toInt()

    /**
     * Extracts each component of the color from the integer representation
     * | 8 bit opacity 0..255 | 8 bit red 0..255 | 8 bit green 0..255 | 8 bit blue 0..255 |
     */
    private fun Int.getOpacity() = ((this shr 24) and 0xff) / 255.0
    private fun Int.getRed() = ((this shr 16) and 0xff) / 255.0
    private fun Int.getGreen() = ((this shr 8) and 0xff) / 255.0
    private fun Int.getBlue() = (this and 0xff) / 255.0

    companion object {
        /**
         * View order of the lowest layer, used for images on frames.
         */
        const val IMAGE_VIEW_ORDER = 0
        const val LANDMARKS_VIEW_ORDER = 1
    }
}
