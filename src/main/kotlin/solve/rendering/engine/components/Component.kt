package solve.rendering.engine.components

import solve.rendering.engine.scene.SceneObject

abstract class Component {
    var sceneObject: SceneObject? = null
        private set

    fun addToGameObject(sceneObject: SceneObject) {
        this.sceneObject?.removeComponent(this)

        this.sceneObject = sceneObject
        sceneObject.addComponent(this)
    }

    open fun start() { }

    open fun update(deltaTime: Float) { }

    open fun enable() { }

    open fun disable() { }

    open fun destroy() { }
}
