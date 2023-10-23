package solve.rendering.engine.components

import solve.rendering.engine.scene.GameObject

abstract class Component {
    var gameObject: GameObject? = null
        private set

    fun addToGameObject(gameObject: GameObject) {
        this.gameObject?.removeComponent(this)

        this.gameObject = gameObject
        gameObject.addComponent(this)
    }

    open fun start() { }

    open fun update(deltaTime: Float) { }

    open fun enable() { }

    open fun disable() { }

    open fun destroy() { }
}
