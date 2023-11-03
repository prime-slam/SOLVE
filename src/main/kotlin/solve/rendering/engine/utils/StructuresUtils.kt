package solve.rendering.engine.utils

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import org.joml.Vector4i

operator fun Vector2f.plus(otherVector: Vector2f): Vector2f = Vector2f(this).add(otherVector)
operator fun Vector3f.plus(otherVector: Vector3f): Vector3f = Vector3f(this).add(otherVector)
operator fun Vector4f.plus(otherVector: Vector4f): Vector4f = Vector4f(this).add(otherVector)

operator fun Vector2f.unaryMinus() = Vector2f(-x, -y)
operator fun Vector3f.unaryMinus() = Vector3f(-x, -y, -z)
operator fun Vector4f.unaryMinus() = Vector4f(-x, -y, -z, -w)

operator fun Vector2f.minus(otherVector: Vector2f) = this + (-otherVector)
operator fun Vector3f.minus(otherVector: Vector3f) = this + (-otherVector)
operator fun Vector4f.minus(otherVector: Vector4f) = this + (-otherVector)

operator fun Vector2f.times(value: Float): Vector2f = Vector2f(this).mul(value)

operator fun Vector3f.times(value: Float): Vector3f = Vector3f(this).mul(value)

operator fun Vector4f.times(value: Float): Vector4f = Vector4f(this).mul(value)

fun Vector2f.toIntVector(): Vector2i = Vector2i(x.toInt(), y.toInt())

fun Vector3f.toIntVector(): Vector3i = Vector3i(x.toInt(), y.toInt(), z.toInt())

fun Vector4f.toIntVector(): Vector4i = Vector4i(x.toInt(), y.toInt(), z.toInt(), w.toInt())

operator fun Vector2i.plus(otherVector: Vector2i): Vector2i = Vector2i(this).add(otherVector)
operator fun Vector3i.plus(otherVector: Vector3i): Vector3i = Vector3i(this).add(otherVector)
operator fun Vector4i.plus(otherVector: Vector4i): Vector4i = Vector4i(this).add(otherVector)

operator fun Vector2i.unaryMinus() = Vector2i(-x, -y)
operator fun Vector3i.unaryMinus() = Vector3i(-x, -y, -z)
operator fun Vector4i.unaryMinus() = Vector4i(-x, -y, -z, -w)

operator fun Vector2i.minus(otherVector: Vector2i) = this + (-otherVector)
operator fun Vector3i.minus(otherVector: Vector3i) = this + (-otherVector)
operator fun Vector4i.minus(otherVector: Vector4i) = this + (-otherVector)

operator fun Vector2i.times(value: Int): Vector2i = Vector2i(this).mul(value)

operator fun Vector3i.times(value: Int): Vector3i = Vector3i(this).mul(value)

operator fun Vector4i.times(value: Int): Vector4i = Vector4i(this).mul(value)

operator fun Matrix4f.times(otherMatrix: Matrix4f): Matrix4f = this.mul(otherMatrix)


fun Vector2f.toList() = listOf(x, y)

fun Vector3f.toList() = listOf(x, y, z)

fun Vector4f.toList() = listOf(x, y, z, w)
