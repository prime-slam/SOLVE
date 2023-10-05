package solve.engine.renderer

import solve.engine.components.SpriteRenderer
import solve.engine.jade.GameObject
import solve.engine.jade.Scene

class Renderer {
    private val MAX_BATCH_SIZE = 1000
    private val batches: MutableList<RenderBatch>

    init {
        batches = ArrayList()
    }

    fun add(go: GameObject) {
        val spr = go.getComponent(SpriteRenderer::class.java)
        spr?.let { add(it) }
    }

    private fun add(sprite: SpriteRenderer) {
        var added = false
        for (batch in batches) {
            if (batch.hasRoom()) {
                val tex = sprite.texture
                if (tex == null || batch.hasTexture(tex) || batch.hasTextureRoom()) {
                    batch.addSprite(sprite)
                    added = true
                    break
                }
            }
        }
        if (!added) {
            val newBatch = RenderBatch(MAX_BATCH_SIZE)
            newBatch.start()
            batches.add(newBatch)
            newBatch.addSprite(sprite)
        }
    }

    fun render(scene: Scene) {
        for (batch in batches) {
            batch.render(scene)
        }
    }
}