package solve.rendering.engine.structure

open class Component(private val gameObject: GameObject) {
    open fun start() { }

    open fun update(deltaTime: Float) { }

    open fun enable() { }

    open fun disable() { }

    open fun destroy() { }
}
