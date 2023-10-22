package solve.rendering.engine.components

import solve.rendering.engine.rendering.texture.Texture
import solve.rendering.engine.scene.GameObject
import solve.rendering.engine.structures.Color

class SpriteRenderer(gameObject: GameObject) : Component(gameObject) {
    var color = Color.white
        private set
    var sprite: Sprite? = null
        private set

    val texture: Texture?
        get() = sprite?.texture

    fun setColor(color: Color) {
        this.color = color
    }

    fun setTexture(texture: Texture) {
        if (sprite?.texture != texture) {
            sprite = Sprite(texture)
        }
    }
}
