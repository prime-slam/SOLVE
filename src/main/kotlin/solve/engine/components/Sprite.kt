package solve.engine.components

import solve.engine.renderer.Texture
import org.joml.Vector2f

class Sprite {
    var texture: Texture?
        private set

    var texCoords: Array<Vector2f>
        private set


    constructor(texture: Texture? = null) {
        this.texture = texture
        val texCoords = arrayOf(
            Vector2f(1f, 1f),
            Vector2f(1f, 0f),
            Vector2f(0f, 0f),
            Vector2f(0f, 1f)
        )
        this.texCoords = texCoords
    }

    constructor(texture: Texture, texCoords: Array<Vector2f>) {
        this.texture = texture
        this.texCoords = texCoords
    }
}