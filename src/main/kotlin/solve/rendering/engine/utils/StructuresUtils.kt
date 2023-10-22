package solve.rendering.engine.utils

import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

fun Vector2f.toList() = listOf(x, y)

fun Vector3f.toList() = listOf(x, y, z)

fun Vector4f.toList() = listOf(x, y, z, w)
