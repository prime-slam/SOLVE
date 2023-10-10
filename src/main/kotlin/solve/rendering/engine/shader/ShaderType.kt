package solve.rendering.engine.shader

import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER

enum class ShaderType {
    VERTEX,
    GEOMETRY,
    FRAGMENT;

    companion object {
        fun getShaderTypeID(type: ShaderType) = when (type) {
            VERTEX -> GL_VERTEX_SHADER
            GEOMETRY -> GL_GEOMETRY_SHADER
            FRAGMENT -> GL_FRAGMENT_SHADER
        }
    }
}
