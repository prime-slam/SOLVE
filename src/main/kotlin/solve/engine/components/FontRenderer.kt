package solve.engine.components

import solve.engine.jade.Component

class FontRenderer : Component() {
    override fun start() {
        if (gameObject?.getComponent(SpriteRenderer::class.java) != null) {
            println("Found Font Renderer!")
        }
    }

    override fun update(dt: Float) {}
}