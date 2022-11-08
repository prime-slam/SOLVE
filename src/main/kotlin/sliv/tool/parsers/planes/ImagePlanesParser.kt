package sliv.tool.parsers.planes

import java.awt.image.DataBufferByte
import kotlin.io.path.Path
import kotlin.io.path.extension
import sliv.tool.parsers.Parser
import sliv.tool.parsers.ParserUtils
import sliv.tool.parsers.structures.Plane
import sliv.tool.scene.model.Point
import java.awt.image.BufferedImage

// Describes types of image formats, according to their color segments number.
private enum class ColorSegmentsType {
    TRIPLE {
        override val colorComponentsNumber = 3
        override val segmentsByteOffset = 0
    },
    QUAD {
        override val colorComponentsNumber = 4
        override val segmentsByteOffset = 1
    };

    abstract val colorComponentsNumber: Int
    abstract val segmentsByteOffset: Int
}

// A parser class for planes stored in images in a form of a mask.
class ImagePlanesParser(private val colorToUIDMap: Map<Int, Long>) : Parser<Plane> {
    companion object {
        private const val ColorsPerSegment = 256
    }

    private fun convertSeparateToWholeRGB(r: Int, g: Int, b: Int): Int =
        (r * ColorsPerSegment * ColorsPerSegment + g * ColorsPerSegment + b)

    private fun getPlanePixelColor(
        pixelIndex: Int,
        imageByteDataArray: ByteArray,
        segmentsType: ColorSegmentsType
    ): Int {
        val segmentsByteOffset = segmentsType.segmentsByteOffset

        return convertSeparateToWholeRGB(
            imageByteDataArray[pixelIndex + segmentsByteOffset + 2].toUByte().toInt(),
            imageByteDataArray[pixelIndex + segmentsByteOffset + 1].toUByte().toInt(),
            imageByteDataArray[pixelIndex + segmentsByteOffset].toUByte().toInt(),
        )
    }

    private fun getImageByteDataArray(image: BufferedImage) = (image.data.dataBuffer as DataBufferByte).data

    private fun determineImageColorSegmentsType(imagePath: String) = when (Path(imagePath).extension.lowercase()) {
        "png" -> ColorSegmentsType.QUAD
        "jpg", "jpeg" -> ColorSegmentsType.TRIPLE
        else -> ColorSegmentsType.TRIPLE.also { println("Unexpected image format while parsing a plane!") }
    }

    private fun getUIDByColor(color: Int) = colorToUIDMap.getOrElse(color) {
            println("The color to uid map does not contains value for color: $color!")
            -1
    }

    override fun parse(filePath: String): List<Plane> {
        val bufferedImage = ParserUtils.loadImage(filePath)

        bufferedImage ?: return emptyList()

        val planePoints = mutableMapOf<Long, MutableList<Point>>()

        val imageByteDataArray = getImageByteDataArray(bufferedImage)
        val colorSegmentsType: ColorSegmentsType = determineImageColorSegmentsType(filePath)
        val colorComponentsNumber = colorSegmentsType.colorComponentsNumber
        val imageWidth = bufferedImage.width.toShort()

        for (i in imageByteDataArray.indices step colorComponentsNumber) {
            val x = i / colorComponentsNumber % imageWidth
            val y = i / colorComponentsNumber / imageWidth

            val pixelColor = getPlanePixelColor(i, imageByteDataArray, colorSegmentsType)
            if (pixelColor != 0) {
                planePoints.getOrPut(getUIDByColor(pixelColor)) { mutableListOf() }.add(Point(x.toShort(), y.toShort()))
            }
        }

        return planePoints.keys.map { Plane(it, planePoints[it] ?: emptyList()) }
    }

    override fun extractUIDs(filePath: String): List<Long> {
        val bufferedImage = ParserUtils.loadImage(filePath)

        bufferedImage ?: return emptyList()

        val uids = mutableSetOf<Long>()

        val imageByteDataArray = getImageByteDataArray(bufferedImage)
        val colorSegmentsType: ColorSegmentsType = determineImageColorSegmentsType(filePath)
        val colorComponentsNumber = colorSegmentsType.colorComponentsNumber
        for (i in imageByteDataArray.indices step colorComponentsNumber) {
            val pixelColor = getPlanePixelColor(i, imageByteDataArray, colorSegmentsType)
            val uid: Long = getUIDByColor(pixelColor)
            if (!uids.contains(uid)) {
                uids.add(uid)
            }
        }

        return uids.toList()
    }
}
