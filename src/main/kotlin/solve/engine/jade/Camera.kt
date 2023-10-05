package solve.engine.jade

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class Camera(var position: Vector2f) {
    val projectionMatrix: Matrix4f

    private val viewMatrix: Matrix4f

    init {
        projectionMatrix = Matrix4f()
        viewMatrix = Matrix4f()
        adjustProjection()
    }

    fun adjustProjection() {
        projectionMatrix.identity()
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f)
    }

    fun getViewMatrix(): Matrix4f {
        val cameraFront = Vector3f(0.0f, 0.0f, -1.0f)
        val cameraUp = Vector3f(0.0f, 1.0f, 0.0f)
        viewMatrix.identity()
        viewMatrix.lookAt(
            Vector3f(position.x, position.y, 20.0f),
            cameraFront.add(position.x, position.y, 0.0f),
            cameraUp
        )
        return viewMatrix
    }
}