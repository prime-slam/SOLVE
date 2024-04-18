package solve.rendering.engine.utils

import org.joml.Vector2f
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun pointToSegmentDistance(
    pointPosition: Vector2f,
    segmentStartPosition: Vector2f,
    segmentEndPosition: Vector2f
): Float {
    return abs(
        (segmentEndPosition.x - segmentStartPosition.x) * (pointPosition.y - segmentStartPosition.y) -
            (pointPosition.x - segmentStartPosition.x) * (segmentEndPosition.y - segmentStartPosition.y)
    ) /
        sqrt(
            (segmentEndPosition.x - segmentStartPosition.x).pow(2) +
                (segmentEndPosition.y - segmentStartPosition.y).pow(2)
        )
}
