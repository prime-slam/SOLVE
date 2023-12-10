package solve.rendering.canvas

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.nio.IntBuffer

class BufferedImageView(private val width: Int, private val height: Int): ImageView() {
    private val buffer: IntBuffer = IntBuffer.allocate(width * height)
    private val pixelBuffer: PixelBuffer<IntBuffer>

    private val _javaFXImage: WritableImage
    val javaFXImage: Image
        get() = _javaFXImage

    val awtImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE)
    val graphics = awtImage.graphics

    init {
        val dataBuffer = awtImage.raster.dataBuffer
        val dataBufferInt = dataBuffer as DataBufferInt
        val rawInts = dataBufferInt.data
        val rawIntsBuffer = IntBuffer.wrap(rawInts)


        pixelBuffer = PixelBuffer(width, height, rawIntsBuffer, PixelFormat.getIntArgbPreInstance())
        _javaFXImage = WritableImage(pixelBuffer)
        image = _javaFXImage
    }

    fun updateBuffer() {
        pixelBuffer.updateBuffer { null }
    }
}