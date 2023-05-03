package solve.scene.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import tornadofx.c

internal class ColorManagerTests {
    @Test
    fun `Generates unique color with string keys`() {
        val colorManager = ColorManager<String>()
        val key1 = "key1"
        val key2 = "key2"
        val color1 = colorManager.getColor(key1)
        val color2 = colorManager.getColor(key2)

        assertNotEquals(color1, color2)
        assertEquals(color1, colorManager.getColor(key1))
        assertEquals(color2, colorManager.getColor(key2))
    }

    @Test
    fun `Generates unique color with object keys`() {
        class KeyClass

        val colorManager = ColorManager<KeyClass>()
        val key1 = KeyClass()
        val key2 = KeyClass()
        val color1 = colorManager.getColor(key1)
        val color2 = colorManager.getColor(key2)

        assertNotEquals(color1, color2)
        assertEquals(color1, colorManager.getColor(key1))
        assertEquals(color2, colorManager.getColor(key2))
    }

    @Test
    fun `Sets color if it wasn't set`() {
        val colorManager = ColorManager<String>()
        val key1 = "key1"
        val color = c("#010101")
        colorManager.setColor(key1, color)
        assertEquals(color, colorManager.getColor(key1))
    }

    @Test
    fun `Sets color if it was already set`() {
        val colorManager = ColorManager<String>()
        val key1 = "key1"
        val color1 = c("#010101")
        val color2 = c("#111111")
        colorManager.getColor(key1)
        colorManager.setColor(key1, color1)
        assertEquals(color1, colorManager.getColor(key1))
        colorManager.setColor(key1, color2)
        assertEquals(color2, colorManager.getColor(key1))
    }
}