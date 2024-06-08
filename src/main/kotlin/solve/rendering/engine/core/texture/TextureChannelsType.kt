package solve.rendering.engine.core.texture

import org.lwjgl.opengl.GL11.GL_RGB
import org.lwjgl.opengl.GL11.GL_RGBA

/**
 * Used to store a type of color encoding of the texture.
 */
enum class TextureChannelsType(val channelsNumber: Int, val openGLType: Int) {
    RGB(3, GL_RGB),
    RGBA(4, GL_RGBA);

    companion object {
        fun getTextureChannelsType(channelsNumber: Int) = when (channelsNumber) {
            3 -> RGB
            4 -> RGBA
            else -> {
                println("Unknown texture channels type format!")
                null
            }
        }
    }
}
