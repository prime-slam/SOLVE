package solve.rendering.engine.scene

import solve.rendering.engine.camera.Camera

class Scene(camera: Camera = Camera(), gameObjects: List<GameObject> = emptyList()) {
    var camera: Camera = camera
        private set

    private val _gameObjects = gameObjects.toMutableList()
    val gameObjects: List<GameObject>
        get() = _gameObjects

    private var isStarted = false

    fun start() {
        _gameObjects.forEach { it.start() }
        isStarted = true
    }

    fun update(deltaTime: Float) {
        _gameObjects.forEach { gameObject ->
            if (gameObject.isDestroyed) {
                removeGameObject(gameObject)
                return@forEach
            }

            gameObject.update(deltaTime)
        }
    }

    fun addGameObject(gameObject: GameObject) {
        if (_gameObjects.contains(gameObject)) {
            println("The scene already contains adding game object ($gameObject)!")
            return
        }
        _gameObjects.add(gameObject)
        if (isStarted) {
            gameObject.start()
        }
    }

    fun removeGameObject(gameObject: GameObject) {
        _gameObjects.remove(gameObject)
    }
}
