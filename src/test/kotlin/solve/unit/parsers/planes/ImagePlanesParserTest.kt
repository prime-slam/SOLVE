package solve.unit.parsers.planes

import io.github.palexdev.mfxcore.utils.fx.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import solve.parsers.planes.ImagePlanesParser
import solve.parsers.planes.ImagePlanesParser.BackgroundColor
import solve.parsers.planes.ImagePlanesParser.ColorBitsNumber
import solve.parsers.structures.Plane
import solve.scene.model.Point
import solve.unit.parsers.ImageFormat
import solve.unit.parsers.createFileWithImageData
import java.awt.image.BufferedImage
import java.io.File

internal class ImagePlanesParserTest {
    // JPG format has not been tested because the data is distorted due to compression.

    private data class PlaneMaskData(val maskImage: BufferedImage, val planes: List<Plane>)

    @Test
    fun `Parsing a planes image file containing data in a standard format`(@TempDir tempFolder: File) {
        val imageDataFile = createFileWithImageData(testData.maskImage, ImageFormat.PNG, tempFolder)

        assertEquals(testData.planes, ImagePlanesParser.parse(imageDataFile.path))
    }

    private fun createOnePixelImage(onePixelColor: Int): BufferedImage {
        val onePixelWritableImage = WritableImage(1, 1)
        onePixelWritableImage.pixelWriter.setColor(0, 0, getColorByIntRGB(onePixelColor))

        return SwingFXUtils.fromFXImage(onePixelWritableImage, null)
    }

    @Test
    fun `Parsing a one pixel image file with an image parser`(@TempDir tempFolder: File) {
        val onePixelColor = 489431
        val onePixelImage = createOnePixelImage(onePixelColor)

        val imageDataFile = createFileWithImageData(onePixelImage, ImageFormat.PNG, tempFolder)

        val onePixelPlane = Plane(onePixelColor.toLong(), listOf(Point(0, 0)))

        assertEquals(listOf(onePixelPlane), ImagePlanesParser.parse(imageDataFile.path))
    }

    @Test
    fun `Parsing a PNG image saved in a JPG file extension with an image parser`(@TempDir tempFolder: File) {
        val imageDataFile = createFileWithImageData(testData.maskImage, ImageFormat.PNG, tempFolder)
        val wrongFormatImageFile = File(tempFolder, "data.jpg")
        imageDataFile.renameTo(wrongFormatImageFile)

        assertEquals(emptyList<Plane>(), ImagePlanesParser.parse(imageDataFile.path))
    }

    @Test
    fun `Parsing a not image file with an image parser`(@TempDir tempFolder: File) {
        val notImageFile = File(tempFolder, "data.png")
        notImageFile.writeText("Text\n")

        assertEquals(emptyList<Plane>(), ImagePlanesParser.parse(notImageFile.path))
    }

    @Test
    fun `Parsing a planes image file with only background color`(@TempDir tempFolder: File) {
        val imageDataFile = createFileWithImageData(testEmptyData.maskImage, ImageFormat.PNG, tempFolder)

        assertEquals(emptyList<Plane>(), ImagePlanesParser.parse(imageDataFile.path))
    }

    @Test
    fun `Extracting UIDs from a planes image file with a standard format`(@TempDir tempFolder: File) {
        val imageDataFile = createFileWithImageData(testData.maskImage, ImageFormat.PNG, tempFolder)

        val testDataUIDs = testData.planes.map { it.uid }

        assertEquals(testDataUIDs, ImagePlanesParser.extractUIDs(imageDataFile.path))
    }

    @Test
    fun `Extracting UIDs from a one pixel image file with an image parser`(@TempDir tempFolder: File) {
        val onePixelColor = 988623
        val onePixelImage = createOnePixelImage(onePixelColor)

        val imageDataFile = createFileWithImageData(onePixelImage, ImageFormat.PNG, tempFolder)

        assertEquals(listOf(onePixelColor.toLong()), ImagePlanesParser.extractUIDs(imageDataFile.path))
    }

    @Test
    fun `Extracting UIDs from a planes image file with only background color`(@TempDir tempFolder: File) {
        val imageDataFile = createFileWithImageData(testEmptyData.maskImage, ImageFormat.PNG, tempFolder)

        assertEquals(emptyList<Long>(), ImagePlanesParser.extractUIDs(imageDataFile.path))
    }

    companion object {
        @TempDir
        lateinit var tempFolder: File

        private val testColors = listOf(11456, 1945321, 743213, 459654, 15647895, 12, 1646545)
        private const val TestImageWidth = 1280
        private const val TestImageHeight = 720
        private val testData = generatePlaneMaskWithGivenPixelColors(TestImageWidth, TestImageHeight, testColors)

        private val testEmptyData =
            generatePlaneMaskWithGivenPixelColors(TestImageWidth, TestImageHeight, listOf(BackgroundColor))

        private const val OneColorComponentMask = (1 shl ColorBitsNumber) - 1

        private fun getColorByIntRGB(rgb: Int): Color {
            val r = rgb shr (ColorBitsNumber * 2) and OneColorComponentMask
            val g = rgb shr ColorBitsNumber and OneColorComponentMask
            val b = rgb and OneColorComponentMask

            return Color(
                r / OneColorComponentMask.toDouble(),
                g / OneColorComponentMask.toDouble(),
                b / OneColorComponentMask.toDouble(),
                1.0
            )
        }

        private fun generatePlaneMaskWithGivenPixelColors(
            width: Int,
            height: Int,
            colors: List<Int>
        ): PlaneMaskData {
            val usedColors = colors.union(listOf(BackgroundColor)).distinct()

            val writableImage = WritableImage(width, height)
            val unpaintedColors = usedColors.toMutableList()
            val planePoints = mutableMapOf<Long, MutableList<Point>>()
            colors.forEach { planePoints[it.toLong()] = mutableListOf() }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val chosenRGBColor = if (unpaintedColors.isEmpty()) {
                        usedColors.random()
                    } else {
                        unpaintedColors.removeFirst()
                    }
                    val chosenColor = getColorByIntRGB(chosenRGBColor)

                    writableImage.pixelWriter.setColor(x, y, chosenColor)
                    planePoints[chosenRGBColor.toLong()]?.add(Point(x.toShort(), y.toShort()))
                }
            }

            val maskImage = SwingFXUtils.fromFXImage(writableImage, null)
            val planes = planePoints.keys.map {
                    uid ->
                Plane(uid, planePoints[uid] ?: emptyList())
            }

            return PlaneMaskData(maskImage, planes)
        }
    }
}
