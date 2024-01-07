package solve.rendering.engine.components

import org.joml.Vector2f
import solve.rendering.engine.core.texture.Texture2D

class Sprite(val texture: Texture2D, uvCoordinates: List<Vector2f> = defaultUVCoordinates) {
    val uvCoordinates: List<Vector2f>

    val width: Int
        get() = texture.width
    val height: Int
        get() = texture.height

    init {
        if (uvCoordinates.count() != UVCoordinatesNumber) {
            println("The number of the uv coordinates is incorrect!")
            this.uvCoordinates = defaultUVCoordinates
        } else {
            this.uvCoordinates = uvCoordinates
        }
    }

    companion object {
        private const val UVCoordinatesNumber = 4
        private val defaultUVCoordinates = listOf(
            Vector2f(0f, 0f),
            Vector2f(0f, 1f),
            Vector2f(1f, 1f),
            Vector2f(1f, 0f)
        )
    }
}
