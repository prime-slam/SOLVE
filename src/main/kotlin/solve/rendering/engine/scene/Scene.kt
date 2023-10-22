package solve.rendering.engine.scene

import solve.rendering.engine.camera.Camera

class Scene(sceneData: SceneData) {
    var camera: Camera
        private set

    private val _gameObjects: MutableList<GameObject>
    val gameObjects: List<GameObject>
        get() = _gameObjects

    private var isStarted = false

    init {
        _gameObjects = sceneData.gameObjects.toMutableList()
        camera = sceneData.camera
    }

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
        if (isStarted)
            gameObject.start()
    }

    fun removeGameObject(gameObject: GameObject) {
        _gameObjects.remove(gameObject)
    }
}
