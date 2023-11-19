package solve.rendering.engine.scene

import solve.rendering.engine.rendering.renderers.Renderer

class Scene(renderers: List<Renderer> = emptyList()) {
    private val _gameObjects = mutableListOf<GameObject>()
    val gameObjects: List<GameObject>
        get() = _gameObjects

    private val _renderers = renderers
    val renderers: List<Renderer>
        get() = _renderers

    fun update(deltaTime: Float) {
        _gameObjects.forEach { gameObject ->
            if (gameObject.isDestroyed) {
                removeGameObject(gameObject)
                return@forEach
            }

            gameObject.update(deltaTime)
            _renderers.forEach { it.render() }
        }
    }

    fun addGameObject(gameObject: GameObject) {
        if (_gameObjects.contains(gameObject)) {
            println("The scene already contains adding game object ($gameObject)!")
            return
        }
        _gameObjects.add(gameObject)
        _renderers.forEach { it.addGameObject(gameObject) }
    }

    fun removeGameObject(gameObject: GameObject) {
        _gameObjects.remove(gameObject)
        _renderers.forEach { it.removeGameObject(gameObject) }
    }
}
