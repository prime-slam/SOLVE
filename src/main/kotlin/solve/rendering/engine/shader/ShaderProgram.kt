package solve.rendering.engine.shader

import org.joml.Matrix2f
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import org.joml.Vector4i
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.GL_COMPILE_STATUS
import org.lwjgl.opengl.GL20.GL_FALSE
import org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH
import org.lwjgl.opengl.GL20.GL_LINK_STATUS
import org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS
import org.lwjgl.opengl.GL20.glAttachShader
import org.lwjgl.opengl.GL20.glCompileShader
import org.lwjgl.opengl.GL20.glCreateProgram
import org.lwjgl.opengl.GL20.glCreateShader
import org.lwjgl.opengl.GL20.glDeleteProgram
import org.lwjgl.opengl.GL20.glGetProgramInfoLog
import org.lwjgl.opengl.GL20.glGetProgrami
import org.lwjgl.opengl.GL20.glGetShaderInfoLog
import org.lwjgl.opengl.GL20.glGetShaderi
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL20.glLinkProgram
import org.lwjgl.opengl.GL20.glShaderSource
import org.lwjgl.opengl.GL20.glUniform1f
import org.lwjgl.opengl.GL20.glUniform1fv
import org.lwjgl.opengl.GL20.glUniform1i
import org.lwjgl.opengl.GL20.glUniform1iv
import org.lwjgl.opengl.GL20.glUniform2f
import org.lwjgl.opengl.GL20.glUniform2i
import org.lwjgl.opengl.GL20.glUniform3f
import org.lwjgl.opengl.GL20.glUniform3i
import org.lwjgl.opengl.GL20.glUniform4f
import org.lwjgl.opengl.GL20.glUniform4i
import org.lwjgl.opengl.GL20.glUniformMatrix2fv
import org.lwjgl.opengl.GL20.glUniformMatrix3fv
import org.lwjgl.opengl.GL20.glUniformMatrix4fv
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL20.glValidateProgram
import solve.rendering.engine.shader.ShaderType.Companion.getShaderTypeID
import solve.utils.readResourcesFileText

class ShaderProgram {
    private val shaderProgramID: Int = glCreateProgram()

    private var isUsed = false

    init {
        if (shaderProgramID == GL_FALSE) {
            println("The shader creation failed!")
        }
    }

    fun addShader(resourcesFilePath: String, shaderType: ShaderType) {
        val shaderText = readResourcesFileText(resourcesFilePath)

        if (shaderText == null) {
            println("The shader file ($resourcesFilePath) cannot be read!")
            return
        }

        compileShader(shaderText, shaderType)
    }

    fun link() {
        glLinkProgram(shaderProgramID)

        val linkStatus = glGetProgrami(shaderProgramID, GL_LINK_STATUS)
        if (linkStatus == GL_FALSE) {
            println("The shader program compilation failed!")
            printShaderProgramInfoLog()
            return
        }

        glValidateProgram(shaderProgramID)
        val validateStatus = glGetProgrami(shaderProgramID, GL_VALIDATE_STATUS)
        if (validateStatus == GL_FALSE) {
            println("Warning validating shader program:")
            printShaderProgramInfoLog()
        }
    }

    fun use() {
        if (isUsed) {
            return
        }

        glUseProgram(shaderProgramID)
        isUsed = true
    }

    fun detach() {
        if (!isUsed) {
            return
        }

        glUseProgram(GL_FALSE)
        isUsed = false
    }

    fun delete() {
        detach()
        if (shaderProgramID != GL_FALSE) {
            glDeleteProgram(shaderProgramID)
        }
    }

    fun uploadInt(variableName: String, value: Int) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform1i(variableLocation, value)
    }

    fun uploadFloat(variableName: String, value: Float) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform1f(variableLocation, value)
    }

    fun uploadVector2f(variableName: String, vector: Vector2f) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform2f(variableLocation, vector.x, vector.y)
    }

    fun uploadVector3f(variableName: String, vector: Vector3f) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform3f(variableLocation, vector.x, vector.y, vector.z)
    }

    fun uploadVector4f(variableName: String, vector: Vector4f) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform4f(variableLocation, vector.x, vector.y, vector.z, vector.w)
    }

    fun uploadVector2i(variableName: String, vector: Vector2i) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform2i(variableLocation, vector.x, vector.y)
    }

    fun uploadVector3i(variableName: String, vector: Vector3i) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform3i(variableLocation, vector.x, vector.y, vector.z)
    }

    fun uploadVector4i(variableName: String, vector: Vector4i) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform4i(variableLocation, vector.x, vector.y, vector.z, vector.w)
    }

    fun uploadMatrix2f(variableName: String, matrix: Matrix2f) {
        val variableLocation = getVariableLocationWithUse(variableName)
        val matrixBuffer = createMatrixFloatBuffer(2)
        matrix.get(matrixBuffer)
        glUniformMatrix2fv(variableLocation, false, matrixBuffer)
    }
    fun uploadMatrix3f(variableName: String, matrix: Matrix3f) {
        val variableLocation = getVariableLocationWithUse(variableName)
        val matrixBuffer = createMatrixFloatBuffer(3)
        matrix.get(matrixBuffer)
        glUniformMatrix3fv(variableLocation, false, matrixBuffer)
    }

    fun uploadMatrix4f(variableName: String, matrix: Matrix4f) {
        val variableLocation = getVariableLocationWithUse(variableName)
        val matrixBuffer = createMatrixFloatBuffer(4)
        matrix.get(matrixBuffer)
        glUniformMatrix4fv(variableLocation, false, matrixBuffer)
    }

    fun uploadTexture(variableName: String, textureID: Int) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform1i(variableLocation, textureID)
    }

    fun uploadIntArray(variableName: String, array: IntArray) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform1iv(variableLocation, array)
    }

    fun uploadFloatArray(variableName: String, array: FloatArray) {
        val variableLocation = getVariableLocationWithUse(variableName)
        glUniform1fv(variableLocation, array)
    }

    private fun createMatrixFloatBuffer(matrixSize: Int) = BufferUtils.createFloatBuffer(matrixSize * matrixSize)

    private fun getVariableLocationWithUse(variableName: String) =
        glGetUniformLocation(shaderProgramID, variableName).also { use() }

    private fun printShaderProgramInfoLog() {
        val infoLogLength = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH)
        val infoLog = glGetProgramInfoLog(infoLogLength, infoLogLength)
        println(infoLog)
    }

    private fun compileShader(shaderText: String, shaderType: ShaderType) {
        val shaderID = glCreateShader(getShaderTypeID(shaderType))
        glShaderSource(shaderID, shaderText)
        glCompileShader(shaderID)

        val compileStatus = glGetShaderi(shaderID, GL_COMPILE_STATUS)
        if (compileStatus == GL_FALSE) {
            println("The $shaderType shader compilation failed!")
            printShaderInfoLog(shaderID)
            return
        }

        glAttachShader(shaderProgramID, shaderID)
    }

    private fun printShaderInfoLog(shaderID: Int) {
        val infoLogLength = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH)
        val infoLog = glGetShaderInfoLog(shaderID, infoLogLength)
        println(infoLog)
    }
}
