package solve.rendering.engine.utils

import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

operator fun Vector2f.plus(otherVector: Vector2f) = Vector2f(x + otherVector.x, y + otherVector.y)
operator fun Vector3f.plus(otherVector: Vector3f) = Vector3f(x + otherVector.x, y + otherVector.y, z + otherVector.z)
operator fun Vector4f.plus(otherVector: Vector4f) =
    Vector4f(x + otherVector.x, y + otherVector.y, z + otherVector.z, w + otherVector.w)

operator fun Vector2f.unaryMinus() = Vector2f(-x, -y)
operator fun Vector3f.unaryMinus() = Vector3f(-x, -y, -z)
operator fun Vector4f.unaryMinus() = Vector4f(-x, -y, -z, -w)

operator fun Vector2f.minus(otherVector: Vector2f) = this + (-otherVector)
operator fun Vector3f.minus(otherVector: Vector3f) = this + (-otherVector)
operator fun Vector4f.minus(otherVector: Vector4f) = this + (-otherVector)

fun Vector2f.toList() = listOf(x, y)

fun Vector3f.toList() = listOf(x, y, z)

fun Vector4f.toList() = listOf(x, y, z, w)
