package solve.rendering.engine.core.texture

import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL12.glTexImage3D
import org.lwjgl.opengl.GL12.glTexSubImage3D
import org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY
import org.lwjgl.opengl.GL30.glGenerateMipmap
import java.nio.ByteBuffer

class ArrayTexture(
    width: Int,
    height: Int,
    channelsType: TextureChannelsType,
    val size: Int,
    filterType: TextureFilterType = TextureFilterType.Smoothed
) : Texture(filterType) {
    override val textureOpenGLType: Int = GL_TEXTURE_2D_ARRAY

    init {
        this.width = width
        this.height = height
        this.channelsType = channelsType

        initialize()
    }

    override fun initializeTextureParams() {
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glGenerateMipmap(GL_TEXTURE_2D_ARRAY)
    }

    fun uploadTexture(textureData: Texture2DData, layerIndex: Int) {
        if (textureData.width != width || textureData.height != height || textureData.channelsType != channelsType) {
            println("Uploading texture has incorrect configuration!")
            return
        }

        glTexSubImage3D(
            GL_TEXTURE_2D_ARRAY,
            0,
            0,
            0,
            layerIndex,
            width,
            height,
            1,
            channelsType.openGLType,
            GL_UNSIGNED_BYTE,
            textureData.data
        )
    }

    override fun initializeTexture() {
        glTexImage3D(
            GL_TEXTURE_2D_ARRAY,
            0,
            channelsType.openGLType,
            width,
            height,
            size,
            0,
            channelsType.openGLType,
            GL_UNSIGNED_BYTE,
            null as ByteBuffer?
        )
    }
}
