package solve.unit.rendering.engine.utils

import org.joml.Vector2f
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import solve.rendering.engine.utils.pointToSegmentDistance
import solve.rendering.engine.utils.pointToSegmentDistanceSquared
import kotlin.math.sqrt

internal class CalculationUtilsTests {
    @Test
    fun `Calculates the square distance from the point to the line segment (1st test)`() {
        val pointPosition = Vector2f(6f, 1f)
        val segmentStartPosition = Vector2f(1f, 3f)
        val segmentEndPosition = Vector2f(1f, 5f)
        assertEquals(
            29f,
            pointToSegmentDistanceSquared(pointPosition, segmentStartPosition, segmentEndPosition)
        )
    }

    @Test
    fun `Calculates the square distance from the point to the line segment (2nd test)`() {
        val pointPosition = Vector2f(4f, 0f)
        val segmentStartPosition = Vector2f(5f, 1f)
        val segmentEndPosition = Vector2f(8f, 4f)
        assertEquals(
            2f,
            pointToSegmentDistanceSquared(pointPosition, segmentStartPosition, segmentEndPosition)
        )
    }

    @Test
    fun `Calculates the square distance from the point to the line segment (3rd test)`() {
        val pointPosition = Vector2f(2f, -2f)
        val segmentStartPosition = Vector2f(-1f, -3f)
        val segmentEndPosition = Vector2f(5f, -1f)
        assertEquals(
            0f,
            pointToSegmentDistanceSquared(pointPosition, segmentStartPosition, segmentEndPosition)
        )
    }

    @Test
    fun `Calculates the square distance from the point to the line segment (4th test)`() {
        val pointPosition = Vector2f(2f, 6f)
        val segmentStartPosition = Vector2f(-2f, 4f)
        val segmentEndPosition = Vector2f(4f, 2f)
        assertEquals(
            10f,
            pointToSegmentDistanceSquared(pointPosition, segmentStartPosition, segmentEndPosition)
        )
    }

    @Test
    fun `Calculates the distance from the point to the line segment`() {
        val pointPosition = Vector2f(2f, -5f)
        val segmentStartPosition = Vector2f(-2f, -2f)
        val segmentEndPosition = Vector2f(-2f, -7f)
        assertEquals(
            4f,
            pointToSegmentDistance(pointPosition, segmentStartPosition, segmentEndPosition)
        )
    }
}