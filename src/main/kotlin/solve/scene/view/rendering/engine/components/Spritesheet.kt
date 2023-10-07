package solve.scene.view.rendering.engine.components

import solve.scene.view.rendering.engine.renderer.Texture
import org.joml.Vector2f

class Spritesheet(texture: Texture?, spriteWidth: Int, spriteHeight: Int, numSprites: Int, spacing: Int) {
    private val texture: Texture
    private val sprites: MutableList<Sprite>

    init {
        sprites = ArrayList()
        require(texture != null)

        this.texture = texture
        var currentX = 0
        var currentY = texture.height - spriteHeight
        for (i in 0 until numSprites) {
            val topY = (currentY + spriteHeight) / texture.height.toFloat()
            val rightX = (currentX + spriteWidth) / texture.width.toFloat()
            val leftX = currentX / texture.width.toFloat()
            val bottomY = currentY / texture.height.toFloat()
            val texCoords = arrayOf(
                Vector2f(rightX, topY),
                Vector2f(rightX, bottomY),
                Vector2f(leftX, bottomY),
                Vector2f(leftX, topY)
            )
            val sprite = Sprite(this.texture, texCoords)
            sprites.add(sprite)
            currentX += spriteWidth + spacing
            if (currentX >= texture.width) {
                currentX = 0
                currentY -= spriteHeight + spacing
            }
        }
    }

    fun getSprite(index: Int): Sprite {
        return sprites[index]
    }
}