package solve.rendering.engine.core.batch

import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW
import org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL15.glDeleteBuffers
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.opengl.GL30.glVertexAttribPointer
import solve.rendering.engine.core.texture.Texture
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.utils.toList

/**
 * Used to draw similar objects in a single draw call.
 * @param maxBatchSize maximum allowed number of the objects in the batch.
 * @param zIndex depth index of the batch.
 * @param primitiveType used to determine the type of the drawing primitives in EBO.
 * @param attributes used to specify the attributes of each vertex.
 */
open class RenderBatch(
    private val maxBatchSize: Int,
    val zIndex: Int,
    val primitiveType: PrimitiveType,
    private val attributes: List<ShaderAttributeType>
) : Comparable<RenderBatch> {
    var isFull = false
        private set
    var isTexturesFull = false
        private set

    private var vboID = 0
    private var vaoID = 0
    private var eboID = 0

    private val textures = mutableListOf<Texture>()

    private val attributesNumber = attributes.sumOf { it.number }
    private val attributesTotalSize = attributes.sumOf { it.size }

    private val verticesDataBufferSize = maxBatchSize * primitiveType.verticesNumber * attributesNumber
    private val verticesDataBuffer = FloatArray(verticesDataBufferSize)
    private var verticesDataBufferIndexPointer = 0

    init {
        initializeBuffers()
        initializeAttributes()
    }

    open fun cleanupData() {
        verticesDataBufferIndexPointer = 0
        isTexturesFull = false
        isFull = false
        textures.clear()
    }

    // Compares batches according to their depth.
    override fun compareTo(other: RenderBatch): Int = batchComparator.compare(this, other)

    // Binds vertices data, attributes data and textures before the rendering.
    fun bind() {
        glBindVertexArray(vaoID)
        attributes.forEachIndexed { index, _ -> glEnableVertexAttribArray(index) }
        textures.forEachIndexed { index, texture -> texture.bindToSlot(index + 1) }
    }

    // Unbinds vertices data, attributes data and textures after the rendering.
    fun unbind() {
        attributes.forEachIndexed { index, _ -> glDisableVertexAttribArray(index) }
        textures.forEach { texture -> texture.unbind() }
        glBindVertexArray(0)
    }

    // Cleans the vertices data buffer.
    fun rebuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferSubData(GL_ARRAY_BUFFER, 0, verticesDataBuffer)
    }

    // Deletes the batch buffers.
    fun deleteBuffers() {
        glDeleteBuffers(vboID)
        glDeleteBuffers(eboID)
        glDeleteVertexArrays(vaoID)
    }

    fun addTexture(texture: Texture): Int {
        val textureID: Int
        if (textures.contains(texture)) {
            textureID = textures.indexOf(texture) + 1
        } else {
            textures.add(texture)
            textureID = textures.lastIndex + 1

            if (textures.count() >= MaxTexturesNumber) {
                isTexturesFull = true
            }
        }

        return textureID
    }

    fun getTextureLocalID(texture: Texture) = textures.indexOf(texture) + 1

    fun removeTexture(texture: Texture): Boolean = textures.remove(texture)

    fun containsTexture(texture: Texture) = textures.contains(texture)

    fun getVerticesNumber(): Int {
        if (verticesDataBufferIndexPointer % attributesNumber != 0) {
            println("The vertices data buffer seems to not have correct amount of data!")
        }

        return verticesDataBufferIndexPointer / attributesNumber / primitiveType.verticesNumber *
            primitiveType.drawingOrderElementsNumber
    }

    private fun checkIfFull() {
        if (verticesDataBufferIndexPointer >= verticesDataBufferSize) {
            isFull = true
        }
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
        attributes.forEachIndexed { index, attribute ->
            glVertexAttribPointer(
                index,
                attribute.number,
                attribute.openGLType,
                false,
                attributesTotalSize,
                attributesSizeOffset
            )
            glEnableVertexAttribArray(index)
            attributesSizeOffset += attribute.size
        }
    }

    // Used to generate vertex indices to specify an order of their drawing.
    private fun generateElementsIndices(): IntArray {
        val elementsBuffer = IntArray(maxBatchSize * primitiveType.drawingOrderElementsNumber)
        for (i in 0 until maxBatchSize) {
            val elementFirstVertexIndex = i * primitiveType.verticesNumber
            val bufferIndexPointer = i * primitiveType.drawingOrderElementsNumber
            primitiveType.verticesDrawingOrder.forEachIndexed { index, vertexIndex ->
                elementsBuffer[bufferIndexPointer + index] = elementFirstVertexIndex + vertexIndex
            }
        }

        return elementsBuffer
    }

    fun pushFloat(value: Float) {
        if (isFull) {
            println("Cannot push a float value! The vertices data buffer is full!")
            return
        }

        verticesDataBuffer[verticesDataBufferIndexPointer++] = value
        checkIfFull()
    }

    fun pushVector2f(vector: Vector2f) {
        vector.toList().forEach { pushFloat(it) }
    }

    fun pushVector3f(vector: Vector3f) {
        vector.toList().forEach { pushFloat(it) }
    }

    fun pushVector4f(vector: Vector4f) {
        vector.toList().forEach { pushFloat(it) }
    }

    fun pushInt(value: Int) {
        pushFloat(value.toFloat())
    }

    companion object {
        const val MaxTexturesNumber = 7

        private val batchComparator = Comparator.comparingInt<RenderBatch> { it.zIndex }
    }
}
