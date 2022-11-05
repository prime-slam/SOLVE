package sliv.tool.parsers.planes

import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.extension
import sliv.tool.parsers.Parser
import sliv.tool.parsers.structures.Plane
import sliv.tool.scene.model.Point

private enum class ColorSegmentsType {
    TRIPLE {
        override fun colorComponentsNumber() = 3

        override fun segmentsByteOffset() = 1
    },

    QUAD {
        override fun colorComponentsNumber() = 4

        override fun segmentsByteOffset() = 0
    };

    abstract fun segmentsByteOffset(): Int

    abstract fun colorComponentsNumber(): Int
}

object ImagePlanesParser : Parser<Plane> {
    private fun isPlanePixel(pixelIndex: Int, imageByteDataArray: ByteArray, segmentsType: ColorSegmentsType): Boolean {
        val segmentsByteOffset = segmentsType.segmentsByteOffset()

        return (imageByteDataArray[pixelIndex + segmentsByteOffset].toUByte() +
            imageByteDataArray[pixelIndex + segmentsByteOffset + 1].toUByte() +
            imageByteDataArray[pixelIndex + segmentsByteOffset + 2].toUByte()) != 0u
    }

    override fun parse(filePath: String): Plane {
        val bufferedImage = ImageIO.read(File(filePath))
        val imageByteDataArray = (bufferedImage.data.dataBuffer as DataBufferByte).data

        val planePoints = mutableListOf<Point>()

        val colorSegmentsType: ColorSegmentsType = when (Path(filePath).extension.lowercase()) {
            "png" -> ColorSegmentsType.QUAD
            "jpg", "jpeg" -> ColorSegmentsType.TRIPLE
            else -> ColorSegmentsType.TRIPLE.also { println("Unexpected image format!") }
        }
        val colorComponentsNumber = colorSegmentsType.colorComponentsNumber()

        val imageWidth = bufferedImage.width.toShort()
        var x: Short = 0
        var y: Short = 0
        for (i in imageByteDataArray.indices step colorComponentsNumber) {
            if (isPlanePixel(i, imageByteDataArray, colorSegmentsType))
                planePoints.add(Point(x, y))

            ++x
            if (x == imageWidth) {
                x = 0
                ++y
            }
        }

        val rgbColor =
            if (planePoints.isNotEmpty())
                bufferedImage.getRGB(planePoints[0].x.toInt(), planePoints[0].y.toInt())
            else
                0

        return Plane(rgbColor, planePoints)
    }
}
