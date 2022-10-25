package sliv.tool.parsers

import sliv.tool.scene.model.Point
import java.io.File
import java.io.FileNotFoundException

// Utility functions used in the parser module.
object ParserUtils {
  fun readFileText(filePath: String): String? {
    var text: String? = null
    try {
      text = File(filePath).readText()
    } catch (exception: FileNotFoundException) {
      println("File not found while reading the text!\n${exception.message}")
    }

    return text
  }

  fun doubleCoordinatesToScenePoint(x: Double, y: Double) =
      Point(x.toInt().toShort(), y.toInt().toShort())
}
