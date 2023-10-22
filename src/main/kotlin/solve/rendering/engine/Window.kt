package solve.rendering.engine

import org.joml.Vector2f
import solve.rendering.engine.camera.Camera
import solve.rendering.engine.scene.Scene

class Window(
    width: Int,
    height: Int,
    camera: Camera = Camera(),
    scene: Scene? = null
) {
    var width = width
        private set
    var height = height
        private set

    var scene: Scene? = scene
        private set
    var camera: Camera = camera
        private set

    fun changeScene(newScene: Scene) {
        scene = newScene
    }

    fun changeCamera(newCamera: Camera) {
        camera = newCamera
    }

    fun resize(newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight
    }

    fun calculateProjectionMatrix() = camera.calculateProjectionMatrix(Vector2f(width.toFloat(), height.toFloat()))
}
