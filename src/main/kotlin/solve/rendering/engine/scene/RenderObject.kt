package solve.rendering.engine.scene

import solve.rendering.engine.components.Component

class RenderObject(
    name: String,
    val transform: Transform = Transform(),
    components: List<Component> = emptyList()
) {
    var name = name
        private set

    private val _components = components.toMutableList()
    val components: List<Component>
        get() = _components

    var isDestroyed = false
        private set

    init {
        _renderObjects.add(this)
        _components.forEach { it.addToRenderObject(this) }
    }

    fun start() {
        components.forEach { it.start() }
    }

    fun update(deltaTime: Float) {
        components.forEach { it.update(deltaTime) }
    }

    fun addComponent(component: Component) {
        if (_components.contains(component)) {
            return
        }

        _components.add(component)
    }

    fun removeComponent(component: Component) {
        _components.remove(component)
    }

    fun setName(name: String) {
        this.name = name
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
        private val _renderObjects = mutableListOf<RenderObject>()
        val renderObjects: List<RenderObject>
            get() = _renderObjects
    }
}
