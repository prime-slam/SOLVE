package solve.rendering.engine.rendering.batch

import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW
import org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL15.glDeleteBuffers
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.opengl.GL30.glGenVertexArrays
import solve.rendering.engine.rendering.texture.Texture
import solve.rendering.engine.shader.ShaderAttributeType

open class RenderBatch(
    private val maxBatchSize: Int,
    val zIndex: Int,
    val primitiveType: PrimitiveType,
    private val attributes: List<ShaderAttributeType>
) {
    var isFull = false
        private set

    private var vboID = 0
    private var vaoID = 0
    private var eboID = 0

    private val textureIDs = mutableListOf<Int>()
    private val textures = mutableListOf<Texture>()

    private val attributesNumber = attributes.sumOf { it.number }
    private val attributesTotalSize = attributes.sumOf { it.size }

    private val verticesDataBuffer = FloatArray(maxBatchSize * primitiveType.verticesNumber * attributesNumber)
    private var verticesDataBufferIndexPointer = 0

    init {
        initializeBuffers()
        initializeAttributes()
    }

    fun bind() {
        glBindVertexArray(vaoID)
        textures.forEachIndexed { index, texture -> texture.bindToSlot(index + 1) }
    }

    fun unbind() {
        textures.forEach { it.unbind() }
        glBindVertexArray(0)
    }

    fun rebuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferSubData(GL_ARRAY_BUFFER, 0, verticesDataBuffer)
    }

    fun deleteBuffers() {
        glDeleteBuffers(vboID)
        glDeleteBuffers(eboID)
        glDeleteVertexArrays(vaoID)
    }

    fun addTexture(texture: Texture): Int {
        var textureID = 0
        if (textures.contains(texture)) {
            textureID = textures.indexOf(texture) + 1
        } else {
            textures.add(texture)
            textureID = textures.lastIndex + 1

            if (textures.count() >= maxBatchSize)
                isFull = true
        }

        return textureID
    }

    fun removeTexture(texture: Texture): Boolean = textures.remove(texture)

    fun containsTexture(texture: Texture) = textures.contains(texture)

    fun getVerticesNumber(): Int {
        if (verticesDataBufferIndexPointer % attributesNumber != 0)
            println("The vertices data buffer seems to not have correct amount of data!")

        return (verticesDataBufferIndexPointer * primitiveType.drawingOrderElementsNumber /
                (attributesNumber * primitiveType.verticesNumber))
    }

    private fun initializeBuffers() {
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)

        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        val verticesBufferSize = (verticesDataBuffer.size * Float.SIZE_BYTES).toLong()
        glBufferData(GL_ARRAY_BUFFER, verticesBufferSize, GL_DYNAMIC_DRAW)

        eboID = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, generateElementsIndices(), GL_STATIC_DRAW)
    }

    private fun initializeAttributes() {
        var attributesSizeOffset = 0L
        for (i in 0 until attributes.count()) {
            val attribute = attributes[i]
            glVertexAttribPointer(
                i,
                attribute.number,
                attribute.openGLType,
                false,
                attributesTotalSize,
                attributesSizeOffset
            )
            glEnableVertexAttribArray(i)
            attributesSizeOffset += attribute.size
        }
    }

    private fun generateElementsIndices(): IntArray {
        val elementsBuffer = IntArray(maxBatchSize * primitiveType.verticesNumber)
        for (i in 0 until maxBatchSize) {
            primitiveType.verticesDrawingOrder.forEach { vertexIndex ->
                elementsBuffer[i * primitiveType.verticesNumber] = i + vertexIndex
            }
        }

        return elementsBuffer
    }
}
