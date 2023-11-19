package solve.rendering.engine.rendering.texture

import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL11.GL_REPEAT
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.glTexImage2D
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load
import solve.utils.getResourceAbsolutePath

class Texture2D(private val resourcesPath: String) : Texture() {
    override val textureOpenGLType: Int = GL_TEXTURE_2D

    init {
        initialize()
    }

    override fun initializeTextureParams() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    }

    override fun initializeTexture() {
        val absolutePath = getResourceAbsolutePath(resourcesPath)
        if (absolutePath == null) {
            println("The path of the reading texture is null!")
            return
        }

        val textureData = loadData(absolutePath)
        if (textureData == null) {
            println("The read texture data is null!")
            return
        }

        width = textureData.width
        height = textureData.height
        channelsType = textureData.channelsType

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            channelsType.openGLType,
            width,
            height,
            0,
            channelsType.openGLType,
            GL_UNSIGNED_BYTE,
            textureData.data
        )

        freeData(textureData)
    }

    companion object {
        fun loadData(path: String): Texture2DData? {
            val widthBuffer = IntArray(1)
            val heightBuffer = IntArray(1)
            val channelsNumberBuffer = IntArray(1)

            stbi_set_flip_vertically_on_load(true)
            val data = stbi_load(path, widthBuffer, heightBuffer, channelsNumberBuffer, 0)

            if (data == null) {
                println("The data of the read texture is null!")
                return null
            }

            val width = widthBuffer.first()
            val height = heightBuffer.first()
            val imageChannelsType = TextureChannelsType.getTextureChannelsType(channelsNumberBuffer.first())
            if (imageChannelsType == null) {
                println("Wrong type of the texture image channels!")
                return null
            }

            return Texture2DData(data, width, height, imageChannelsType)
        }

        fun freeData(textureData: Texture2DData) {
            stbi_image_free(textureData.data)
        }
    }
}
