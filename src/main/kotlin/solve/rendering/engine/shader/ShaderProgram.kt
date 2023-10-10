package solve.rendering.engine.shader

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
import org.lwjgl.opengl.GL20.glUniform1i
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL20.glValidateProgram
import solve.rendering.engine.shader.ShaderType.Companion.getShaderTypeID
import solve.utils.readFileText

class ShaderProgram {
    private val shaderProgramID: Int = glCreateProgram()

    private var isUsed = false

    init {
        if (shaderProgramID == GL_FALSE) {
            println("The shader creation failed!")
        }
    }

    fun addShader(resourcesFilePath: String, shaderType: ShaderType) {
        val shaderText = readFileText(resourcesFilePath)

        if (shaderText == null) {
            println("The shader file ($resourcesFilePath) cannot be read!")
            return
        }

        compileShader(shaderText, shaderType)
    }

    fun compile() {
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

        glUseProgram(0)
        isUsed = false
    }

    fun cleanup() {
        detach()
        if (shaderProgramID != GL_FALSE) {
            glDeleteProgram(shaderProgramID)
        }
    }

    fun uploadInt(variableName: String, value: Int) {
        val variableLocation = getVariableLocation(variableName)
        use()
        glUniform1i(variableLocation, value)
    }

    fun uploadFloat(variableName: String, value: Float) {
        val variableLocation = getVariableLocation(variableName)
        use()
        glUniform1f(variableLocation, value)
    }

    private fun getVariableLocation(variableName: String) = glGetUniformLocation(shaderProgramID, variableName)

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
