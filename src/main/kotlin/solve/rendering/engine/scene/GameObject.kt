package solve.rendering.engine.scene

import solve.rendering.engine.components.Component

class GameObject(private val name: String) {
    val transform = Transform()
    private val _components = mutableListOf<Component>()
    val components: List<Component>
        get() = _components

    var isDestroyed = false
        private set

    init {
        _gameObjects.add(this)
    }

    fun start() {
        components.forEach { it.start() }
    }

    fun update(deltaTime: Float) {
        components.forEach { it.update(deltaTime) }
    }

    fun addComponent(component: Component) {
        _components.add(component)
    }

    fun removeComponent(component: Component) {
        _components.remove(component)
    }

    fun hasComponent(component: Component) = components.contains(component)

    inline fun <reified T : Component> getComponentOfType(): T? {
        return components.firstOrNull { it is T } as? T?
    }

    fun enable() {
        components.forEach { it.enable() }
    }

    fun disable() {
        components.forEach { it.disable() }
    }

    fun destroy() {
        components.forEach { it.destroy() }
        isDestroyed = true
    }

    companion object {
        private val _gameObjects = mutableListOf<GameObject>()
        val gameObjects: List<GameObject>
            get() = _gameObjects
    }
}
