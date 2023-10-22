package solve.rendering.engine.structures

import org.joml.Vector4f

class Color(r: Float, g: Float, b: Float, a: Float = 1f) {
    var r = r
        private set
    var g = g
        private set
    var b = b
        private set
    var a = a
        private set

    init {
        if (r !in componentValueRange ||
            g !in componentValueRange ||
            b !in componentValueRange ||
            a !in componentValueRange
        ) {
            println("The colors components values should be in 0..1 range!")
            white.copyTo(this)
        }
    }

    fun toVector4f(): Vector4f {
        return Vector4f(r, g, b, a)
    }

    fun copyTo(otherColor: Color) {
        otherColor.r = r
        otherColor.g = g
        otherColor.b = b
        otherColor.a = a
    }

    companion object {
        private val componentValueRange = 0f..1f
        val white = Color(1f, 1f, 1f, 1f)
        val black = Color(0f, 0f, 0f, 0f)
    }
}
