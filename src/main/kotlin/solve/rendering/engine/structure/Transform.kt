package solve.rendering.engine.structure

import org.joml.Vector2f

data class Transform(
    private val position: Vector2f = Vector2f(),
    private val rotation: Vector2f = Vector2f(),
    private val scale: Vector2f = Vector2f(),
    private val zIndex: Float = 0f
)
