package solve.parsers

import solve.scene.model.Point

// Utility functions used in the parser module.
object ParserUtils {
    fun doubleCoordinatesToScenePoint(x: Double, y: Double) = Point(x.toInt().toShort(), y.toInt().toShort())
}
