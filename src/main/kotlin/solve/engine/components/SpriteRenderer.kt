package solve.engine.components

import solve.engine.jade.Component
import solve.engine.renderer.Texture
import org.joml.Vector2f
import org.joml.Vector4f

class SpriteRenderer : Component {
    var color: Vector4f
        private set
    private var sprite: Sprite

    constructor(color: Vector4f) {
        this.color = color
        sprite = Sprite()
    }

    constructor(sprite: Sprite) {
        this.sprite = sprite
        color = Vector4f(1f, 1f, 1f, 1f)
    }

    override fun start() {}

    override fun update(dt: Float) {}

    val texture: Texture?
        get() = sprite.texture

    val texCoords: Array<Vector2f>
        get() = sprite.texCoords
}