package solve.scene.view.drawing

import javafx.scene.paint.Color
import solve.scene.model.Point
import solve.scene.model.createPairs
import solve.scene.view.NULL_COLOR
import java.util.SortedSet

class FrameDrawer(private val canvas: BufferedImageView) {
    val width = canvas.roundedWidth
    val height = canvas.roundedHeight

    private val buffer = Array<SortedSet<FrameElement>>(width * height) { sortedSetOf() }

    fun clear() {
        buffer.forEach {
            it.clear()
        }
        fullRedraw()
    }

    fun addElement(element: FrameElement) {
        element.points.forEach {
            buffer[it.x + width * it.y].add(element)
        }
        redrawPoints(element.points)
    }

    fun removeElement(element: FrameElement) {
        element.points.forEach { point ->
            buffer[point.x + width * point.y].remove(element)
        }
        redrawPoints(element.points)
    }

    fun elementUpdated(element: FrameElement) {
        redrawPoints(element.points)
    }

    private fun fullRedraw() {
        redrawPoints(createPairs(width.toShort(), height.toShort()))
    }

    private fun redrawPoints(points: Iterable<Point>) {
        val pixels = points.map { point -> BufferedImageView.Pixel(getPixelColor(point), point) }
        canvas.drawPixels(pixels)
    }

    private fun getPixelColor(point: Point): Color {
        return buffer[point.x + width * point.y].fold(NULL_COLOR) { sum, new ->
            overlayColors(new.getColor(point), sum)
        }
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
}