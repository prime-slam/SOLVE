package solve.parsers

import solve.parsers.planes.ImagePlanesParser
import solve.scene.model.Point
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

// Utility functions used in the parser module.
object ParserUtils {
    fun doubleCoordinatesToScenePoint(x: Double, y: Double) = Point(x.toInt().toShort(), y.toInt().toShort())
}
