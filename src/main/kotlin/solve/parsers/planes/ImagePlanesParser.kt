package solve.parsers.planes

import java.awt.image.DataBufferByte
import solve.parsers.Parser
import solve.parsers.structures.Plane
import solve.scene.model.Point
import solve.utils.loadBufferedImage
import java.awt.image.BufferedImage

// A parser class for planes stored in images in a form of a mask.
object ImagePlanesParser : Parser<Plane> {
    // Describes types of image formats, according to their color segments number.
    private enum class ColorSegmentsType(val colorComponentsNumber: Int, val segmentsByteOffset: Int) {
        Triple(3, 0),
        Quad(4, 1);

        companion object {
            fun getColorSegmentsType(colorComponentsNumber: Int) = when (colorComponentsNumber) {
                3 -> Triple
                4 -> Quad
                else -> null.also {
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

    private inline fun BufferedImage.forEachPixelColor(action: (index: Int, color: Int) -> Unit) {
        val imageByteDataArray = getImageByteDataArray(this)
        val colorSegmentsType: ColorSegmentsType =
            ColorSegmentsType.getColorSegmentsType(this.colorModel.numComponents) ?: return
        val colorComponentsNumber = colorSegmentsType.colorComponentsNumber

        for (i in imageByteDataArray.indices step colorComponentsNumber) {
            val pixelColor = getPlanePixelColor(i, imageByteDataArray, colorSegmentsType)
            action(i / colorComponentsNumber, pixelColor)
        }
    }

    override fun parse(filePath: String): List<Plane> {
        val bufferedImage = loadBufferedImage(filePath) ?: return emptyList()

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
        val bufferedImage = loadBufferedImage(filePath) ?: return emptyList()

        val uids = mutableSetOf<Long>()

        bufferedImage.forEachPixelColor { _, color ->
            val uid = color.toLong()
            uids.add(uid)
        }

        return uids.toList()
    }
}
