package solve.rendering.canvas

import javafx.beans.InvalidationListener
import org.joml.Vector2f
import solve.rendering.engine.rendering.renderers.FramesRenderer
import solve.rendering.engine.scene.Scene
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.utils.ServiceLocator
import tornadofx.*

class SceneCanvas : OpenGLCanvas() {
    private var sceneController: SceneController? = null
    private var framesRenderer: FramesRenderer? = null
    private var canvasScene: Scene? = null

    private val projectChangedEventHandler = InvalidationListener {
        val controller = sceneController ?: return@InvalidationListener
        framesRenderer?.setNewSceneFrames(controller.scene.frames)
    }
    private val framesChangedEventHandler = InvalidationListener {
        val controller = sceneController ?: return@InvalidationListener
        framesRenderer?.setFramesSelection(controller.scene.frames)
    }

    init {
        initializeCanvasEvents()
    }

    override fun onInit() {
        moveCamerToLeftUpperCorner()

        val controller = ServiceLocator.getService<SceneController>() ?: return
        sceneController = controller

        val renderer = FramesRenderer(window)
        framesRenderer = renderer
        canvasScene = Scene(listOf(renderer))

        addBindings()
    }

    override fun onDraw(deltaTime: Float) {
        canvasScene?.renderers?.forEach { it.render() }
    }

    override fun onDispose() {
        removeBindings()
    }

    private fun moveCamerToLeftUpperCorner() {
        window.camera.position =
            Vector2f(window.width.toFloat(), window.height.toFloat()) / (2f * window.camera.scaledZoom)
    }

    private fun addBindings() {
        SceneFacade.lastVisualizationKeepSettingsProperty.addListener(projectChangedEventHandler)
        sceneController?.sceneProperty?.addListener(framesChangedEventHandler)
    }

    private fun removeBindings() {
        SceneFacade.lastVisualizationKeepSettingsProperty.removeListener(projectChangedEventHandler)
        sceneController?.sceneProperty?.removeListener(framesChangedEventHandler)
    }
}
