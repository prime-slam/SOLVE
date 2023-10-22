package solve.rendering.engine.scene

import org.joml.Vector2f

data class Transform(
    val position: Vector2f = Vector2f(),
    val rotation: Float = 0f,
    val scale: Vector2f = Vector2f(),
    val zIndex: Int = 0
)
