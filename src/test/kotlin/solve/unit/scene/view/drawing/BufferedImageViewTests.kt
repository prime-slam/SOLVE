package solve.unit.scene.view.drawing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import solve.scene.model.Point
import solve.scene.view.drawing.BufferedImageView

internal class BufferedImageViewTests {
    @Test
    fun `Rounds size to the bigger number`() {
        val width = 10.2
        val height = 6.1
        val bufferedImageView = BufferedImageView(width, height, 1.0)

        assertEquals(7, bufferedImageView.roundedHeight)
        assertEquals(11, bufferedImageView.roundedWidth)
    }

    @Test
    fun `Rounds size correctly if size is integer`() {
        val width = 10.0
        val height = 6.0
        val bufferedImageView = BufferedImageView(width, height, 1.0)

        assertEquals(6, bufferedImageView.roundedHeight)
        assertEquals(10, bufferedImageView.roundedWidth)
    }

    @Test
    fun `Rounded size doesn't depend on scale`() {
        val width = 10.2
        val height = 6.1
        val bufferedImageView = BufferedImageView(width, height, 1.0)
        bufferedImageView.scale(5.0)

        assertEquals(7, bufferedImageView.roundedHeight)
        assertEquals(11, bufferedImageView.roundedWidth)
    }

    @Test
    fun `Scales image view`() {
        val width = 10.2
        val height = 6.1
        val bufferedImageView = BufferedImageView(width, height, 1.0)
        val scale = 5.0
        bufferedImageView.scale(scale)

        assertEquals(height * scale, bufferedImageView.fitHeight)
        assertEquals(width * scale, bufferedImageView.fitWidth)
    }

    @Test
    fun `Set pixel value changes image buffer`() {
        val bufferedImageView = BufferedImageView(10.0, 5.0, 1.0)
        val newColor = 100
        bufferedImageView.setPixelValue(Point(1, 1), newColor)
        assertEquals(newColor, bufferedImageView.image.pixelReader.getArgb(1, 1))
    }
}