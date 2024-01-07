package solve.rendering.engine.scene

import solve.rendering.engine.core.renderers.Renderer

class Scene(renderers: List<Renderer> = emptyList()) {
    private val _renderObjects = mutableListOf<RenderObject>()
    val renderObjects: List<RenderObject>
        get() = _renderObjects

    private val _renderers = renderers
    val renderers: List<Renderer>
        get() = _renderers

    fun update(deltaTime: Float) {
        _renderObjects.forEach { gameObject ->
            if (gameObject.isDestroyed) {
                removeRenderObject(gameObject)
                return@forEach
            }

            gameObject.update(deltaTime)
            _renderers.forEach { it.render() }
        }
    }

    fun addRenderObject(renderObject: RenderObject) {
        if (_renderObjects.contains(renderObject)) {
            println("The scene already contains adding game object ($renderObject)!")
            return
        }
        _renderObjects.add(renderObject)
        _renderers.forEach { it.addRenderObject(renderObject) }
    }

    fun removeRenderObject(renderObject: RenderObject) {
        _renderObjects.remove(renderObject)
        _renderers.forEach { it.removeRenderObject(renderObject) }
    }
}
