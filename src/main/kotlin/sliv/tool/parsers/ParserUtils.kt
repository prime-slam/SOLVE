package sliv.tool.parsers

import sliv.tool.parsers.planes.ImagePlanesParser
import sliv.tool.scene.model.Point
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

// Utility functions used in the parser module.
object ParserUtils {
    fun readFileText(filePath: String): String? {
        val file = File(filePath)

        if (!file.canRead()) {
            println("The text file cannot be read!")
            return null
        }

        var text: String? = null
        try {
            text = file.readText()
        } catch (exception: IOException) {
            println("Input error while reading the text!\n${exception.message}")
        }

        return text
    }

    fun loadImage(filePath: String): BufferedImage? {
        val file = File(filePath)

        if (!file.canRead()) {
            println("The image file cannot be read!")
            return null
        }

        var image: BufferedImage? = null
        try {
            image = ImageIO.read(file)
        } catch (exception: IOException) {
            println("Input error while loading the image!\n${exception.message}")
        }

        return image
    }

    fun doubleCoordinatesToScenePoint(x: Double, y: Double) = Point(x.toInt().toShort(), y.toInt().toShort())
}
