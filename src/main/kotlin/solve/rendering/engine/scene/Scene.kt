package solve.rendering.engine.scene

import solve.rendering.engine.rendering.renderers.Renderer

class Scene(renderers: List<Renderer> = emptyList()) {
    private val _sceneObjects = mutableListOf<SceneObject>()
    val sceneObjects: List<SceneObject>
        get() = _sceneObjects

    private val _renderers = renderers
    val renderers: List<Renderer>
        get() = _renderers

    fun update(deltaTime: Float) {
        _sceneObjects.forEach { gameObject ->
            if (gameObject.isDestroyed) {
                removeGameObject(gameObject)
                return@forEach
            }

            gameObject.update(deltaTime)
            _renderers.forEach { it.render() }
        }
    }

    fun addGameObject(sceneObject: SceneObject) {
        if (_sceneObjects.contains(sceneObject)) {
            println("The scene already contains adding game object ($sceneObject)!")
            return
        }
        _sceneObjects.add(sceneObject)
        _renderers.forEach { it.addGameObject(sceneObject) }
    }

    fun removeGameObject(sceneObject: SceneObject) {
        _sceneObjects.remove(sceneObject)
        _renderers.forEach { it.removeGameObject(sceneObject) }
    }
}
