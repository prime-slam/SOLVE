package solve.rendering.engine.components

import solve.rendering.engine.scene.GameObject

abstract class Component(val gameObject: GameObject) {
    open fun start() { }

    open fun update(deltaTime: Float) { }

    open fun enable() { }

    open fun disable() { }

    open fun destroy() { }
}
