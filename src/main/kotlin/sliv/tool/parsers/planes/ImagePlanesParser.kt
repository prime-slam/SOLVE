package sliv.tool.parsers.planes

import java.awt.image.DataBufferByte
import sliv.tool.parsers.Parser
import sliv.tool.parsers.ParserUtils
import sliv.tool.parsers.structures.Plane
import sliv.tool.scene.model.Point
import java.awt.image.BufferedImage

// A parser class for planes stored in images in a form of a mask.
object ImagePlanesParser : Parser<Plane> {
    // Describes types of image formats, according to their color segments number.
    private enum class ColorSegmentsType(val colorComponentsNumber: Int, val segmentsByteOffset: Int) {
        TRIPLE(3, 0),
        QUAD(4, 1);

        companion object {
            fun getColorSegmentsType(colorComponentsNumber: Int) = when (colorComponentsNumber) {
                3 -> TRIPLE
                4 -> QUAD
                else -> TRIPLE.also {
                    println("Unexpected color components number: $colorComponentsNumber!")
                }
            }
        }
    }

    private const val COLOR_BITS_NUMBER = 8

    private fun convertSeparateToWholeRGB(r: UByte, g: UByte, b: UByte): Int =
        (r.toInt() shl COLOR_BITS_NUMBER * 2) + (g.toInt() shl COLOR_BITS_NUMBER) + b.toInt()

    private fun getPlanePixelColor(
        pixelIndex: Int,
        imageByteDataArray: ByteArray,
        segmentsType: ColorSegmentsType
    ): Int {
        val segmentsByteOffset = segmentsType.segmentsByteOffset

        return convertSeparateToWholeRGB(
            imageByteDataArray[pixelIndex + segmentsByteOffset + 2].toUByte(),
            imageByteDataArray[pixelIndex + segmentsByteOffset + 1].toUByte(),
            imageByteDataArray[pixelIndex + segmentsByteOffset].toUByte(),
        )
    }

    private fun getImageByteDataArray(image: BufferedImage) = (image.data.dataBuffer as DataBufferByte).data

    private fun BufferedImage.forEachPixelColor(action: (index: Int, color: Int) -> Unit) {
        val imageByteDataArray = getImageByteDataArray(this)
        val colorSegmentsType: ColorSegmentsType =
            ColorSegmentsType.getColorSegmentsType(this.colorModel.numComponents)
        val colorComponentsNumber = colorSegmentsType.colorComponentsNumber

        for (i in imageByteDataArray.indices step colorComponentsNumber) {
            val pixelColor = getPlanePixelColor(i, imageByteDataArray, colorSegmentsType)
            action(i / colorComponentsNumber, pixelColor)
        }
    }

    override fun parse(filePath: String): List<Plane> {
        val bufferedImage = ParserUtils.loadImage(filePath) ?: return emptyList()

        val planePoints = mutableMapOf<Long, MutableList<Point>>()

        val imageWidth = bufferedImage.width

        bufferedImage.forEachPixelColor { i, color ->
            val x = i % imageWidth
            val y = i / imageWidth

            if (color != 0) {
                planePoints.getOrPut(color.toLong()) { mutableListOf() }.add(Point(x.toShort(), y.toShort()))
            }
        }

        return planePoints.keys.map { Plane(it, planePoints[it] ?: emptyList()) }
    }

    override fun extractUIDs(filePath: String): List<Long> {
        val bufferedImage = ParserUtils.loadImage(filePath) ?: return emptyList()

        val uids = mutableListOf<Long>()

        bufferedImage.forEachPixelColor { _, color ->
            val uid = color.toLong()
            if (!uids.contains(uid)) {
                uids.add(uid)
            }
        }

        return uids.toList()
    }
}
