package solve.rendering.engine.utils

import org.joml.Vector2f
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

fun pointToSegmentDistanceSquared(
    pointPosition: Vector2f,
    segmentStartPosition: Vector2f,
    segmentEndPosition: Vector2f
): Float {
    val segmentLength = segmentStartPosition.distanceSquared(segmentEndPosition)
    if (segmentLength == 0f)
        return pointPosition.distanceSquared(segmentStartPosition)

    var t = ((pointPosition.x - segmentStartPosition.x) * (segmentEndPosition.x - segmentStartPosition.x) +
            (pointPosition.y - segmentStartPosition.y) * (segmentEndPosition.y - segmentStartPosition.y)) / segmentLength
    t = max(0f, min(1f, t))
    val a = Vector2f(segmentStartPosition.x + t * (segmentEndPosition.x - segmentStartPosition.x),
        segmentStartPosition.y + t * (segmentEndPosition.y - segmentStartPosition.y))

    return pointPosition.distanceSquared(a)
}

fun pointToSegmentDistance(
    pointPosition: Vector2f,
    segmentStartPosition: Vector2f,
    segmentEndPosition: Vector2f
) : Float {
    return sqrt(pointToSegmentDistanceSquared(pointPosition, segmentStartPosition, segmentEndPosition))
}
