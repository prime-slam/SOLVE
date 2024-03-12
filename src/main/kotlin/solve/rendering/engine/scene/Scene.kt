package solve.rendering.engine.scene

import solve.rendering.engine.core.renderers.FramesRenderer
import solve.rendering.engine.core.renderers.Renderer

class Scene(val framesRenderer: FramesRenderer) {
    private val _renderObjects = mutableListOf<RenderObject>()
    val renderObjects: List<RenderObject>
        get() = _renderObjects

    private val _landmarkRenderers = mutableListOf<Renderer>()
    val landmarkRenderers: List<Renderer>
        get() = _landmarkRenderers

    fun update(deltaTime: Float) {
        _renderObjects.forEach { gameObject ->
            if (gameObject.isDestroyed) {
                removeRenderObject(gameObject)
                return@forEach
            }

            gameObject.update(deltaTime)
        }
        render()
    }

    fun addLandmarkRenderer(renderer: Renderer) {
        _landmarkRenderers.add(renderer)
    }

    fun clearLandmarkRenderers() {
        landmarkRenderers.forEach { it.delete() }
        _landmarkRenderers.clear()
    }

    fun addRenderObject(renderObject: RenderObject) {
        if (_renderObjects.contains(renderObject)) {
            println("The scene already contains adding game object ($renderObject)!")
            return
        }
        _renderObjects.add(renderObject)
        landmarkRenderers.forEach { it.addRenderObject(renderObject) }
    }

    fun removeRenderObject(renderObject: RenderObject) {
        _renderObjects.remove(renderObject)
        landmarkRenderers.forEach { it.removeRenderObject(renderObject) }
    }

    private fun render() {
        framesRenderer.render()
        landmarkRenderers.forEach { it.render() }
    }
}
