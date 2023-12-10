package solve.rendering.canvas

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import java.nio.IntBuffer

class WritableImageView(private val width: Int, private val height: Int) : ImageView() {
    private val rawInts: IntArray
    private val buffer: IntBuffer = IntBuffer.allocate(width * height)
    private val pixelBuffer: PixelBuffer<IntBuffer>

    init {
        rawInts = buffer.array()
        pixelBuffer = PixelBuffer(width, height, buffer, PixelFormat.getIntArgbPreInstance())
        val writableImage = WritableImage(pixelBuffer)
        image = writableImage
    }

    var pixels: IntArray
        get() = rawInts
        set(rawPixels) {
            System.arraycopy(rawPixels, 0, rawInts, 0, rawPixels.size)
        }

    fun drawImage(x0: Int, y0: Int, image: Image, w: Int, h: Int) {
        val sourceImageReader = image.pixelReader
        val scaleX = w.toFloat() / image.width
        val scaleY = h.toFloat() / image.height
        val sourceImageW = image.width.toInt()
        val sourceImageH = image.height.toInt()

        for (x in 0 until sourceImageW) {
            for (y in 0 until sourceImageH) {
                setArgb(
                    x0 + (x * scaleX).toInt(),
                    y0 + (y * scaleY).toInt(),
                    sourceImageReader.getArgb(x, y)
                )
            }
        }
    }

    private fun setArgb(x: Int, y: Int, colorARGB: Int) {
        rawInts[x % width + y * width] = colorARGB
    }

    fun updateBuffer() {
        pixelBuffer.updateBuffer { null }
    }
}