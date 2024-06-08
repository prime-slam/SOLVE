package solve.unit.rendering.engine.structures

import org.joml.Vector4f
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import solve.rendering.engine.structures.Color

internal class ColorTests {
    @Test
    fun `Converts a color to a vector`() {
        val colorVector = testColor.toVector4f()
        assertEquals(colorVector, Vector4f(0.5f, 0.25f, 0.75f, 0.9f))
    }

    @Test
    fun `Copies color components to another color`() {
        val color = Color(1f, 1f, 1f, 1f)
        testColor.copyTo(color)
        assertEquals(testColor, color)
    }

    @Test
    fun `Compares same colors`() {
        val firstColor = Color(0.5f, 1f, 0.25f, 1f)
        val secondColor = Color(0.5f, 1f, 0.25f, 1f)
        assert(firstColor == secondColor)
    }

    companion object {
        val testColor = Color(0.5f, 0.25f, 0.75f, 0.9f)
    }
}
