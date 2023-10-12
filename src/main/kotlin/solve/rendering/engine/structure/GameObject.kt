package solve.rendering.engine.structure

class GameObject(private val name: String) {
    private val transform = Transform()
    private val components = mutableListOf<Component>()

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
        components.add(component)
    }

    fun removeComponent(component: Component) {
        components.remove(component)
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