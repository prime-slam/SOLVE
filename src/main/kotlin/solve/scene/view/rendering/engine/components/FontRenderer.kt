package solve.scene.view.rendering.engine.components

import solve.scene.view.rendering.engine.jade.Component

class FontRenderer : Component() {
    override fun start() {
        if (gameObject?.getComponent(SpriteRenderer::class.java) != null) {
            println("Found Font Renderer!")
        }
    }

    override fun update(dt: Float) {}
}