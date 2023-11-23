package solve.rendering.canvas

import org.joml.Vector2f
import solve.rendering.engine.rendering.renderers.FramesRenderer
import solve.rendering.engine.scene.Scene
import solve.scene.controller.SceneController
import solve.utils.ServiceLocator

class SceneCanvas : OpenGLCanvas() {
    private var sceneController: SceneController? = null
    private var framesRenderer: FramesRenderer? = null
    private var canvasScene: Scene? = null

    init {
        initializeCanvasEvents()
    }

    override fun onInit() {
        moveCamerToLeftUpperCorner()

        val controller = ServiceLocator.getService<SceneController>() ?: return
        sceneController = controller
        val renderer = FramesRenderer(window, controller)
        framesRenderer = renderer
        canvasScene = Scene(listOf(renderer))
    }

    override fun onDraw(deltaTime: Float) {
        canvasScene?.renderers?.forEach { it.render() }
    }

    private fun moveCamerToLeftUpperCorner() {
        window.camera.position =
            Vector2f(window.width.toFloat(), window.height.toFloat()) / (2f * window.camera.scaledZoom)
    }
}
