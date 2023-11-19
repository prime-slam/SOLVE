package solve.interactive.scene.view.drawing

import javafx.scene.image.WritableImage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import solve.interactive.InteractiveTestClass
import solve.scene.model.Point
import tornadofx.c

@ExtendWith(ApplicationExtension::class)
internal class FrameDrawerTests : InteractiveTestClass() {
    @Test
    fun `Calculates its size`() {
        val width = 10.2
        val height = 6.1
        val bufferedImageView = BufferedImageView(width, height, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 0)
        assertEquals(11, frameDrawer.width)
        assertEquals(7, frameDrawer.height)
    }

    @Test
    fun `Invalid layers count`() {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        assertThrows<NegativeArraySizeException> { FrameDrawer(bufferedImageView, -1) }
    }

    @Test
    fun `Rectangle frame element`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val color = c("FFFFF0")
        val rectangle = RectangleFrameElement(0, color, frameDrawer.width, frameDrawer.height)
        frameDrawer.addOrUpdateElement(rectangle)
        robot.interact {
            frameDrawer.fullRedraw()
        }
        val imagePixelReader = bufferedImageView.image.pixelReader

        assertEquals(color, imagePixelReader.getColor(5, 3))
        assertEquals(color, imagePixelReader.getColor(0, 0))
        assertEquals(color, imagePixelReader.getColor(0, 5))
        assertEquals(color, imagePixelReader.getColor(9, 5))
        assertEquals(color, imagePixelReader.getColor(9, 0))
    }

    @Test
    fun `Image frame element`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val image = WritableImage(10, 6)

        val color1 = c("FFFFF0")
        image.pixelWriter.setColor(5, 3, color1)
        val color2 = c("F00000")
        image.pixelWriter.setColor(0, 0, color2)
        val color3 = c("FF0000")
        image.pixelWriter.setColor(0, 5, color3)
        val color4 = c("FFF000")
        image.pixelWriter.setColor(9, 5, color4)
        val color5 = c("FFFF00")
        image.pixelWriter.setColor(9, 0, color5)

        val imageFrameElement = ImageFrameElement(0, image)
        frameDrawer.addOrUpdateElement(imageFrameElement)
        robot.interact {
            frameDrawer.fullRedraw()
        }

        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(color1, pixelReader.getColor(5, 3))
        assertEquals(color2, pixelReader.getColor(0, 0))
        assertEquals(color3, pixelReader.getColor(0, 5))
        assertEquals(color4, pixelReader.getColor(9, 5))
        assertEquals(color5, pixelReader.getColor(9, 0))
    }

    @Test
    fun `Frame element is bigger than canvas`() {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val image = WritableImage(20, 15)
        val imageFrameElement = ImageFrameElement(0, image)
        assertThrows<ArrayIndexOutOfBoundsException> { frameDrawer.addOrUpdateElement(imageFrameElement) }
    }

    @Test
    fun `Frame element is smaller than canvas`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val image = WritableImage(5, 3)

        val color1 = c("FFFFF0")
        image.pixelWriter.setColor(2, 1, color1)
        val color2 = c("F00000")
        image.pixelWriter.setColor(0, 0, color2)
        val color3 = c("FF0000")
        image.pixelWriter.setColor(0, 2, color3)
        val color4 = c("FFF000")
        image.pixelWriter.setColor(4, 2, color4)
        val color5 = c("FFFF00")
        image.pixelWriter.setColor(4, 0, color5)

        val imageFrameElement = ImageFrameElement(0, image)
        frameDrawer.addOrUpdateElement(imageFrameElement)
        robot.interact {
            frameDrawer.fullRedraw()
        }

        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(color1, pixelReader.getColor(2, 1))
        assertEquals(color2, pixelReader.getColor(0, 0))
        assertEquals(color3, pixelReader.getColor(0, 2))
        assertEquals(color4, pixelReader.getColor(4, 2))
        assertEquals(color5, pixelReader.getColor(4, 0))
    }

    @Test
    fun `Addition or updating of element without redrawing doesn't affect image`() {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val rectangle = RectangleFrameElement(0, c("FFFFF0"), frameDrawer.width, frameDrawer.height)
        frameDrawer.addOrUpdateElement(rectangle)
        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(0, pixelReader.getArgb(5, 3))
    }

    @Test
    fun `Changes in element without redrawing doesn't affect image`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(5.0, 3.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val image = WritableImage(5, 3)

        val color1 = c("FFFF0F")
        image.pixelWriter.setColor(2, 1, color1)
        val imageFrameElement = ImageFrameElement(0, image)
        frameDrawer.addOrUpdateElement(imageFrameElement)
        robot.interact {
            frameDrawer.fullRedraw()
        }
        image.pixelWriter.setColor(2, 1, c("FF00FF"))
        frameDrawer.addOrUpdateElement(imageFrameElement)
        assertEquals(color1, bufferedImageView.image.pixelReader.getColor(2, 1))
    }

    @Test
    fun `Changes in element is applied after pixel redraw`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(5.0, 3.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val image = WritableImage(5, 3)

        val color1 = c("FFFF0F")
        image.pixelWriter.setColor(2, 1, color1)
        val imageFrameElement = ImageFrameElement(0, image)
        frameDrawer.addOrUpdateElement(imageFrameElement)
        robot.interact {
            frameDrawer.fullRedraw()
        }
        val color2 = c("FF00FF")
        image.pixelWriter.setColor(2, 1, color2)
        frameDrawer.addOrUpdateElement(imageFrameElement)
        robot.interact {
            frameDrawer.redrawPoints(listOf(Point(2, 1)))
        }
        assertEquals(color2, bufferedImageView.image.pixelReader.getColor(2, 1))
    }

    @Test
    fun `Overwrite old element if new element was added to occupied layer`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)

        val color1 = c("FFFFF0")
        val rectangle1 = RectangleFrameElement(0, color1, frameDrawer.width, frameDrawer.height)
        frameDrawer.addOrUpdateElement(rectangle1)
        robot.interact {
            frameDrawer.fullRedraw()
        }

        val color2 = c("FF00FF")
        val rectangle2 = RectangleFrameElement(0, color2, 2, 2)

        frameDrawer.addOrUpdateElement(rectangle2)
        robot.interact {
            frameDrawer.fullRedraw()
        }

        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(color2, pixelReader.getColor(1, 1))
        assertEquals(color1, pixelReader.getColor(2, 2))
    }

    @Test
    fun `Add new element to not existing layer`() {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val frameElement = RectangleFrameElement(2, c("FFFFFF"), 10, 6)
        assertThrows<ArrayIndexOutOfBoundsException> { frameDrawer.addOrUpdateElement(frameElement) }
    }

    @Test
    fun `Add two overlapping layers`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 2)

        val color1 = c("FFFFF0")
        val image1 = WritableImage(10, 6)
        image1.pixelWriter.setColor(1, 1, color1)
        image1.pixelWriter.setColor(1, 2, color1)
        val imageFrameElement1 = ImageFrameElement(0, image1)

        val color2 = c("FF0FF0")
        val image2 = WritableImage(10, 6)
        image2.pixelWriter.setColor(1, 1, color2)
        image2.pixelWriter.setColor(2, 2, color2)
        val imageFrameElement2 = ImageFrameElement(1, image2)

        frameDrawer.addOrUpdateElement(imageFrameElement1)
        frameDrawer.addOrUpdateElement(imageFrameElement2)
        robot.interact {
            frameDrawer.fullRedraw()
        }

        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(color1, pixelReader.getColor(1, 2))
        assertEquals(color2, pixelReader.getColor(1, 1))
        assertEquals(color2, pixelReader.getColor(2, 2))
    }

    @Test
    fun `Overlap two layers with opacity more transparent above`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 2)

        val color1 = c("0A0A0A", 0.7)
        val image1 = WritableImage(10, 6)
        image1.pixelWriter.setColor(1, 1, color1)
        val imageFrameElement1 = ImageFrameElement(0, image1)

        val color2 = c("060606", 0.5)
        val image2 = WritableImage(10, 6)
        image2.pixelWriter.setColor(1, 1, color2)
        val imageFrameElement2 = ImageFrameElement(1, image2)

        frameDrawer.addOrUpdateElement(imageFrameElement1)
        frameDrawer.addOrUpdateElement(imageFrameElement2)
        robot.interact {
            frameDrawer.redrawPoints(listOf(Point(1, 1)))
        }

        val expectedMixedColor = c("080808", 0.848)
        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(expectedMixedColor.toString(), pixelReader.getColor(1, 1).toString())
    }

    @Test
    fun `Overlap two layers with opacity more transparent below`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 2)

        val color1 = c("060606", 0.5)
        val image1 = WritableImage(10, 6)
        image1.pixelWriter.setColor(1, 1, color1)
        val imageFrameElement1 = ImageFrameElement(0, image1)

        val color2 = c("0A0A0A", 0.7)
        val image2 = WritableImage(10, 6)
        image2.pixelWriter.setColor(1, 1, color2)
        val imageFrameElement2 = ImageFrameElement(1, image2)

        frameDrawer.addOrUpdateElement(imageFrameElement1)
        frameDrawer.addOrUpdateElement(imageFrameElement2)
        robot.interact {
            frameDrawer.redrawPoints(listOf(Point(1, 1)))
        }

        val expectedMixedColor = c("0B0B0B", 0.848)
        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(expectedMixedColor.toString(), pixelReader.getColor(1, 1).toString())
    }

    @Test
    fun `Overlap three layers`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 3)

        val color1 = c("FFFFF0")
        val image1 = WritableImage(10, 6)
        image1.pixelWriter.setColor(1, 1, color1)
        image1.pixelWriter.setColor(1, 2, color1)
        val imageFrameElement1 = ImageFrameElement(0, image1)

        val color2 = c("FF0FF0")
        val image2 = WritableImage(10, 6)
        image2.pixelWriter.setColor(1, 1, color2)
        image2.pixelWriter.setColor(2, 2, color2)
        val imageFrameElement2 = ImageFrameElement(1, image2)

        val color3 = c("F00FF0")
        val image3 = WritableImage(10, 6)
        image3.pixelWriter.setColor(1, 1, color3)
        image3.pixelWriter.setColor(3, 3, color3)
        val imageFrameElement3 = ImageFrameElement(2, image3)

        frameDrawer.addOrUpdateElement(imageFrameElement1)
        frameDrawer.addOrUpdateElement(imageFrameElement2)
        frameDrawer.addOrUpdateElement(imageFrameElement3)
        robot.interact {
            frameDrawer.fullRedraw()
        }

        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(color1, pixelReader.getColor(1, 2))
        assertEquals(color2, pixelReader.getColor(2, 2))
        assertEquals(color3, pixelReader.getColor(3, 3))
        assertEquals(color3, pixelReader.getColor(1, 1))
    }

    @Test
    fun `Overlap three layers with opacity`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 3)

        val color1 = c("0A0A0A", 0.8)
        val image1 = WritableImage(10, 6)
        image1.pixelWriter.setColor(1, 1, color1)
        val imageFrameElement1 = ImageFrameElement(0, image1)

        val color2 = c("060606", 0.4)
        val image2 = WritableImage(10, 6)
        image2.pixelWriter.setColor(1, 1, color2)
        val imageFrameElement2 = ImageFrameElement(1, image2)

        val color3 = c("040404", 0.2)
        val image3 = WritableImage(10, 6)
        image3.pixelWriter.setColor(1, 1, color3)
        val imageFrameElement3 = ImageFrameElement(2, image3)

        frameDrawer.addOrUpdateElement(imageFrameElement1)
        frameDrawer.addOrUpdateElement(imageFrameElement2)
        frameDrawer.addOrUpdateElement(imageFrameElement3)
        robot.interact {
            frameDrawer.redrawPoints(listOf(Point(1, 1)))
        }

        val expectedMixedColor = c("070707", 0.903)
        val pixelReader = bufferedImageView.image.pixelReader
        assertEquals(expectedMixedColor.toString(), pixelReader.getColor(1, 1).toString())
    }

    @Test
    fun `Clear without redrawing doesn't affect visible image`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val color = c("FFFFF0")
        val rectangle = RectangleFrameElement(0, color, frameDrawer.width, frameDrawer.height)
        frameDrawer.addOrUpdateElement(rectangle)
        robot.interact {
            frameDrawer.fullRedraw()
        }
        frameDrawer.clear()
        val imagePixelReader = bufferedImageView.image.pixelReader
        assertEquals(color, imagePixelReader.getColor(5, 3))
    }

    @Test
    fun `Clear drops all pixels`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 1)
        val color = c("FFFFF0")
        val rectangle = RectangleFrameElement(0, color, frameDrawer.width, frameDrawer.height)
        frameDrawer.addOrUpdateElement(rectangle)
        robot.interact {
            frameDrawer.fullRedraw()
        }
        frameDrawer.clear()
        robot.interact {
            frameDrawer.fullRedraw()
        }
        val imagePixelReader = bufferedImageView.image.pixelReader
        assertEquals(0, imagePixelReader.getArgb(5, 3))
    }

    @Test
    fun `Clear drops all pixels if there are more than one layer`(robot: FxRobot) {
        val bufferedImageView = BufferedImageView(10.0, 6.0, 1.0)
        val frameDrawer = FrameDrawer(bufferedImageView, 2)
        val color1 = c("FFFFF0")
        val color2 = c("F0F0F0")
        val rectangle1 = RectangleFrameElement(0, color1, frameDrawer.width, frameDrawer.height)
        val rectangle2 = RectangleFrameElement(0, color2, frameDrawer.width, frameDrawer.height)
        frameDrawer.addOrUpdateElement(rectangle1)
        frameDrawer.addOrUpdateElement(rectangle2)
        robot.interact {
            frameDrawer.fullRedraw()
        }
        frameDrawer.clear()
        robot.interact {
            frameDrawer.fullRedraw()
        }
        val imagePixelReader = bufferedImageView.image.pixelReader
        assertEquals(0, imagePixelReader.getArgb(5, 3))
    }
}
