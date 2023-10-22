package solve.rendering.engine.shader

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_INT

enum class ShaderAttributeType(val number: Int, val size: Int, val openGLType: Int) {
    INT(1, Int.SIZE_BYTES, GL_INT),
    INT2(2, 2 * Int.SIZE_BYTES, GL_INT),
    INT3(3, 3 * Int.SIZE_BYTES, GL_INT),
    INT4(4, 4 * Int.SIZE_BYTES, GL_INT),
    FLOAT(1, Float.SIZE_BYTES, GL_FLOAT),
    FLOAT2(2, 2 * Float.SIZE_BYTES, GL_FLOAT),
    FLOAT3(3, 3 * Float.SIZE_BYTES, GL_FLOAT),
    FLOAT4(4, 4 * Float.SIZE_BYTES, GL_FLOAT),
    MAT2(9, 2 * 2 * Float.SIZE_BYTES, GL_FLOAT),
    MAT3(9, 3 * 3 * Float.SIZE_BYTES, GL_FLOAT),
    MAT4(16, 4 * 4 * Float.SIZE_BYTES, GL_FLOAT)
}
