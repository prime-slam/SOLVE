package solve.parsers.planes

import solve.parsers.Parser
import solve.parsers.structures.Plane
import solve.scene.model.Point
import solve.utils.loadBufferedImage
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

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

    const val ColorBitsNumber = 8
    const val BackgroundColor = 0

    override fun parse(filePath: String): List<Plane> {
        val bufferedImage = loadBufferedImage(filePath) ?: return emptyList()

        val planePoints = mutableMapOf<Long, MutableList<Point>>()

        val imageWidth = bufferedImage.width

        bufferedImage.forEachPixelColor { i, color ->
            val x = i % imageWidth
            val y = i / imageWidth

            if (color != BackgroundColor) {
                planePoints.getOrPut(color.toLong()) { mutableListOf() }.add(Point(x.toShort(), y.toShort()))
            }
        }

        return planePoints.keys.map { Plane(it) }
    }

    override fun extractUIDs(filePath: String): List<Long> {
        val bufferedImage = loadBufferedImage(filePath) ?: return emptyList()

        val uids = mutableSetOf<Long>()

        bufferedImage.forEachPixelColor { _, color ->
            if (color != BackgroundColor) {
                val uid = color.toLong()
                uids.add(uid)
            }
        }

        return uids.toList()
    }

    fun getPixelColor(filePath: String, pixelPosition: Point) : Int? {
        val bufferedImage = loadBufferedImage(filePath) ?: return null

        return bufferedImage.getPixelColor(pixelPosition)
    }

    private fun convertSeparateToWholeRGB(r: UByte, g: UByte, b: UByte): Int =
        (r.toInt() shl ColorBitsNumber * 2) + (g.toInt() shl ColorBitsNumber) + b.toInt()

    private fun getImageByteDataPixelColor(
        pixelIndex: Int,
        imageByteDataArray: ByteArray,
        segmentsType: ColorSegmentsType
    ): Int {
        val segmentsByteOffset = segmentsType.segmentsByteOffset

        return convertSeparateToWholeRGB(
            imageByteDataArray[pixelIndex + segmentsByteOffset + 2].toUByte(),
            imageByteDataArray[pixelIndex + segmentsByteOffset + 1].toUByte(),
            imageByteDataArray[pixelIndex + segmentsByteOffset].toUByte()
        )
    }

    private fun getImageByteDataArray(image: BufferedImage) = (image.data.dataBuffer as DataBufferByte).data

    private inline fun BufferedImage.forEachPixelColor(action: (index: Int, color: Int) -> Unit) {
        val imageByteDataArray = getImageByteDataArray(this)
        val colorSegmentsType: ColorSegmentsType =
            ColorSegmentsType.getColorSegmentsType(colorModel.numComponents) ?: return
        val colorComponentsNumber = colorSegmentsType.colorComponentsNumber

        for (i in imageByteDataArray.indices step colorComponentsNumber) {
            val pixelColor = getImageByteDataPixelColor(i, imageByteDataArray, colorSegmentsType)
            action(i / colorComponentsNumber, pixelColor)
        }
    }

    private fun BufferedImage.getPixelColor(pixelPosition: Point) : Int? {
        val imageByteDataArray = getImageByteDataArray(this)
        val colorSegmentsType: ColorSegmentsType =
            ColorSegmentsType.getColorSegmentsType(colorModel.numComponents) ?: return null
        val colorComponentsNumber = colorSegmentsType.colorComponentsNumber
        val pixelFirstComponentIndex = (pixelPosition.y * this.width + pixelPosition.x) * colorComponentsNumber

        return getImageByteDataPixelColor(pixelFirstComponentIndex, imageByteDataArray, colorSegmentsType)
    }
}
