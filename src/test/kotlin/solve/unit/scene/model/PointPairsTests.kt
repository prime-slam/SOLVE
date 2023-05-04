package solve.unit.scene.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import solve.scene.model.Point
import solve.scene.model.PointPairs
import solve.scene.model.Size

internal class PointPairsTests {
    @Test
    fun `Creates pairs with the specified size`() {
        val width = 100
        val height = 50
        val size = Size(width, height)
        val pairs = PointPairs.getPairs(size)
        assertEquals(width * height, pairs.size)
        assertTrue(pairs.contains(Point((width - 1).toShort(), (height - 1).toShort())))
        assertTrue(pairs.contains(Point(0, 0)))
        assertTrue(pairs.contains(Point(50, 25)))
    }

    @Test
    fun `Creates pairs with the specified size once`() {
        val width = 100
        val height = 50
        val size1 = Size(width, height)
        val size2 = Size(width, height)
        val pairs1 = PointPairs.getPairs(size1)
        val pairs2 = PointPairs.getPairs(size2)
        assertSame(pairs1, pairs2)
    }

    @Test
    fun `Creates pairs for the different sizes`() {
        val width = 100
        val height = 50
        val size1 = Size(width, height)
        val size2 = Size(height, width)
        val pairs1 = PointPairs.getPairs(size1)
        val pairs2 = PointPairs.getPairs(size2)
        assertNotSame(pairs1, pairs2)
        assertEquals(pairs2.size, width * height)
        assertTrue(pairs2.contains(Point((height - 1).toShort(), (width - 1).toShort())))
    }
}