package solve.rendering.engine.rendering.texture

import com.huskerdev.openglfx.core.GL_RGBA
import org.lwjgl.opengl.GL11.GL_RGB

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