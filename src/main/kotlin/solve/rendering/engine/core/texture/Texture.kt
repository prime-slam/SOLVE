package solve.rendering.engine.core.texture

import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glDeleteTextures
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13.GL_TEXTURE0

abstract class Texture {
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

    fun bind() {
        glBindTexture(textureOpenGLType, textureID)
    }

    fun unbind() {
        glBindTexture(textureOpenGLType, 0)
    }

    fun bindToSlot(unit: Int) {
        if (unit <= 0) {
            println("The slot unit should be a positive number!")
            return
        }

        GL13.glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(textureOpenGLType, textureID)
    }

    fun delete() {
        glDeleteTextures(textureID)
    }

    protected fun initialize() {
        bind()
        initializeTexture()
        initializeTextureParams()
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
}
