package solve.rendering.engine

import org.joml.Vector2f
import org.joml.Vector2i
import solve.rendering.engine.camera.Camera
import solve.rendering.engine.scene.Scene
import solve.rendering.engine.utils.minus

class Window(
    width: Int,
    height: Int,
    camera: Camera = Camera(),
    scene: Scene? = null
) {
    val size: Vector2i
        get() = Vector2i(width, height)

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

    fun calculateTopLeftCornerShaderPosition(): Vector2f {
        return camera.position - screenToShaderVector(Vector2i(size) / 2f)
    }

    // Converts screen vector to shader coordinates.
    // One frame excluding spacing area corresponds to a (1, framesRatio) shader vector.
    fun screenToShaderVector(screenVector: Vector2i): Vector2f {
        return Vector2f(screenVector) / camera.scaledZoom
    }
}
