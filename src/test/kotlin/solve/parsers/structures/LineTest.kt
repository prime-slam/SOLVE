package solve.parsers.structures

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LineTest {
    @Test
    fun toStringTest() {
        val testLine = Line(89L, 129.45, 0.0, -78.456, 11.5)

        assertEquals("89,129.45,0.0,-78.456,11.5", testLine.toString())
    }
}
