package solve.engine.jade

import solve.engine.renderer.Renderer

abstract class Scene {
    protected var renderer = Renderer()
    protected var camera: Camera? = null
    private var isRunning = false
    protected var gameObjects: MutableList<GameObject> = ArrayList()
    open fun init() {}
    fun start() {
        for (go in gameObjects) {
            go.start()
            renderer.add(go)
        }
        isRunning = true
    }

    fun addGameObjectToScene(go: GameObject) {
        if (!isRunning) {
            gameObjects.add(go)
        } else {
            gameObjects.add(go)
            go.start()
            renderer.add(go)
        }
    }

    abstract fun update(dt: Float)

    fun camera(): Camera? {
        return camera
    }
}