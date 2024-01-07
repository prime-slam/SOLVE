package solve.rendering.engine.components

import solve.rendering.engine.scene.RenderObject

abstract class Component {
    var renderObject: RenderObject? = null
        private set

    fun addToRenderObject(renderObject: RenderObject) {
        this.renderObject?.removeComponent(this)

        this.renderObject = renderObject
        renderObject.addComponent(this)
    }

    open fun start() { }

    open fun update(deltaTime: Float) { }

    open fun enable() { }

    open fun disable() { }

    open fun destroy() { }
}
