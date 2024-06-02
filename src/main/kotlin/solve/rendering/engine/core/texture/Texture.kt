package solve.rendering.engine.core.texture

import org.joml.Vector2f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL11.GL_NEAREST
import org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glDeleteTextures
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture

enum class TextureFilterType(val openGLParamValue: Int) {
    PixelPerfect(GL_NEAREST),
    Smoothed(GL_LINEAR)
}

abstract class Texture(private val filterType: TextureFilterType = TextureFilterType.Smoothed) {
    protected abstract val textureOpenGLType: Int

    val textureID: Int = glGenTextures()
    var width = 0
        protected set
    var height = 0
        protected set
    var channelsType = TextureChannelsType.RGB
        protected set

    protected abstract fun initializeTextureParams()

    protected abstract fun initializeTexture()

    private fun initializeTextureFilterParams() {
        glTexParameteri(textureOpenGLType, GL_TEXTURE_MIN_FILTER, filterType.openGLParamValue)
        glTexParameteri(textureOpenGLType, GL_TEXTURE_MAG_FILTER, filterType.openGLParamValue)
    }

    fun bind() {
        glBindTexture(textureOpenGLType, textureID)
        println("bind ${GL11.glGetError()}")
    }

    fun unbind() {
        glBindTexture(textureOpenGLType, 0)
        println("unbind ${GL11.glGetError()}")
    }

    fun bindToSlot(unit: Int) {
        if (unit <= 0) {
            println("The slot unit should be a positive number!")
            return
        }

        glActiveTexture(GL_TEXTURE0 + unit)
        println("bindToSlot ${GL11.glGetError()}")
        glBindTexture(textureOpenGLType, textureID)
        println("bindToSlot ${GL11.glGetError()}")
    }

    fun delete() {
        glDeleteTextures(textureID)
        println("delete ${GL11.glGetError()}")
    }

    protected fun initialize() {
        bind()
        initializeTexture()
        initializeTextureFilterParams()
        initializeTextureParams()
        println("initialize ${GL11.glGetError()}")
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Texture) {
            return false
        }

        return width == other.width &&
            height == other.height &&
            textureID == other.textureID &&
            textureOpenGLType == other.textureOpenGLType
    }

    override fun hashCode(): Int {
        val primeNumber = 31
        var result = textureOpenGLType
        result = primeNumber * result + textureID
        result = primeNumber * result + width
        result = primeNumber * result + height
        result = primeNumber * result + channelsType.hashCode()

        return result
    }

    companion object {
        val defaultUVCoordinates = listOf(
            Vector2f(0f, 0f),
            Vector2f(0f, 1f),
            Vector2f(1f, 1f),
            Vector2f(1f, 0f)
        )
    }
}
