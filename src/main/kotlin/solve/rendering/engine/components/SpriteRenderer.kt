package solve.rendering.engine.components

import solve.rendering.engine.core.texture.Texture2D
import solve.rendering.engine.structures.Color

class SpriteRenderer() : Component() {
    var color = Color.white
        private set
    var sprite: Sprite? = null
        private set

    val texture: Texture2D?
        get() = sprite?.texture

    constructor(texture: Texture2D) : this() {
        setTexture(texture)
    }

    constructor(sprite: Sprite) : this() {
        setSprite(sprite)
    }

    fun setColor(color: Color) {
        this.color = color
    }

    fun setSprite(sprite: Sprite) {
        this.sprite = sprite
    }

    fun setTexture(texture: Texture2D) {
        if (sprite?.texture != texture) {
            sprite = Sprite(texture)
        }
    }
}
