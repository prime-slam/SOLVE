package solve.unit.rendering.engine.structures

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import solve.rendering.engine.structures.IntRect

internal class IntRectTests {
    @Test
    fun `Create an IntRect object and checks calculation of the x1 and y1 coordinates`() {
        val intRect = IntRect(2, 5, 4, 7)
        assertEquals(5, intRect.x1)
        assertEquals(11, intRect.y1)
    }
}
