package solve.parsers.structures

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PointTest {
    @Test
    fun toStringTest() {
        val testPoint = Point(145L, 12.156, -45.12)

        assertEquals("145,12.156,-45.12", testPoint.toString())
    }
}