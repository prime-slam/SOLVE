package solve.scene.view.drawing

import javafx.scene.image.ImageView
import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import solve.scene.model.Point
import solve.utils.ceilToInt
import java.nio.IntBuffer

/**
 * Encapsulates pixel drawing, derives from ImageView to display drawn image.
 * Uses integer matrix [width x height] as buffer to make drawing faster.
 * Buffer size doesn't depend on current scaled width and height,
 * visible size grows due to scale transformations.
 *
 * In application all drawing on frames implemented with this class,
 * including images, planes and rectangles, except association adorners.
 */
// Encapsulates pixel drawing
class BufferedImageView(
    private val width: Double,
    private val height: Double,
    initialScale: Double
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

    /**
     * Scales visible image.
     */
    fun scale(scale: Double) {
        this.fitWidth = width * scale
        this.fitHeight = height * scale
    }

    /**
     * Image buffer width.
     */
    val roundedWidth: Int get() = width.ceilToInt()

    /**
     * Image buffer height.
     */
    val roundedHeight: Int get() = height.ceilToInt()

    /**
     * Fills pixel in the specified point with color.
     * Throws IndexOutOfBoundsException if point is not within buffer.
     *
     * @param color color in argb format.
     */
    fun setPixelValue(point: Point, color: Int) {
        this.pixels[point.x + roundedWidth * point.y] = color
    }

    /**
     * Refreshes visible image.
     * When pixel value was set visible image is not updated instantly,
     * call this function after all needed pixels has been filled.
     */
    fun redraw() {
        pixelBuffer.updateBuffer { null }
    }
}
