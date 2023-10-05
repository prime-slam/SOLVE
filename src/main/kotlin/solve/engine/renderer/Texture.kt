package solve.engine.renderer

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage

class Texture(private val filepath: String) {
    private val texID: Int
    var width = 0
    var height = 0

    init {

        // Generate texture on GPU
        texID = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID)

        // Set texture parameters
        // Repeat image in both directions
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
        // When stretching the image, pixelate
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        // When shrinking an image, pixelate
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)
        STBImage.stbi_set_flip_vertically_on_load(true)
        val image = STBImage.stbi_load(filepath, width, height, channels, 0)
        if (image != null) {
            this.width = width[0]
            this.height = height[0]
            if (channels[0] == 3) {
                GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width[0], height[0],
                    0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image
                )
            } else if (channels[0] == 4) {
                GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width[0], height[0],
                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image
                )
            } else {
                assert(false) { "Error: (Texture) Unknown number of channesl '" + channels[0] + "'" }
            }
        } else {
            assert(false) { "Error: (Texture) Could not load image '$filepath'" }
        }
        STBImage.stbi_image_free(image!!)
    }

    fun bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID)
    }

    fun unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }
}