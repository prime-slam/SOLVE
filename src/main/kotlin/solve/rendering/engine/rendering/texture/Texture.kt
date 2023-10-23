package solve.rendering.engine.rendering.texture

import org.lwjgl.opengl.GL11.GL_NEAREST
import org.lwjgl.opengl.GL11.GL_REPEAT
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL11.glTexImage2D
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load
import solve.utils.getResourceAbsolutePath

class Texture(private val resourcesPath: String) {
    val textureID = glGenTextures()
    var width = 0
        private set
    var height = 0
        private set
    var channelsType = TextureChannelsType.RGB
        private set

    init {
        initializeTextureParams()
        loadTexture()
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, textureID)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun bindToSlot(unit: Int) {
        if (unit <= 0) {
            println("The slot unit should be a positive number!")
            return
        }

        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_2D, textureID)
    }

    private fun initializeTextureParams() {
        glBindTexture(GL_TEXTURE_2D, textureID)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    }

    private fun loadTexture() {
        val absoluteImagePath = getResourceAbsolutePath(resourcesPath)
        if (absoluteImagePath == null) {
            println("The path of the reading texture is null!")
            return
        }

        val widthBuffer = IntArray(1)
        val heightBuffer = IntArray(1)
        val channelsNumberBuffer = IntArray(1)

        stbi_set_flip_vertically_on_load(true)
        val image = stbi_load(absoluteImagePath, widthBuffer, heightBuffer, channelsNumberBuffer, 0)

        if (image == null) {
            println("The image of the read texture is null!")
            return
        }

        width = widthBuffer.first()
        height = heightBuffer.first()
        val imageChannelsType = TextureChannelsType.getTextureChannelsType(channelsNumberBuffer.first())
        if (imageChannelsType == null) {
            println("Wrong type of the texture image channels!")
            return
        }

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            channelsType.openGLType,
            width,
            height,
            0,
            channelsType.openGLType,
            GL_UNSIGNED_BYTE,
            image
        )
        stbi_image_free(image)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Texture) {
            return false
        }

        return width == other.width &&
            height == other.height &&
            textureID == other.textureID &&
            resourcesPath == other.resourcesPath
    }

    override fun hashCode(): Int {
        val primeNumber = 31
        var result = resourcesPath.hashCode()
        result = primeNumber * result + textureID
        result = primeNumber * result + width
        result = primeNumber * result + height
        result = primeNumber * result + channelsType.hashCode()

        return result
    }
}
