package solve.rendering.engine.camera

import org.joml.Matrix4f
import org.joml.Vector2f
import solve.utils.times

class Camera(var position: Vector2f = Vector2f(), zoom: Float = 1f) {
    var zoom: Float = zoom
        set(value) {
            if (value <= 0f) {
                println("The camera scale should be positive number!")
                return
            }
            field = value
        }

    fun calculateProjectionMatrix(projectionSize: Vector2f): Matrix4f {
        val orthoMatrix = calculateOrthoMatrix(projectionSize)
        val zoomMatrix = calculateZoomMatrix()

        return orthoMatrix * zoomMatrix
    }

    private fun calculateZoomMatrix(): Matrix4f = Matrix4f().scale(zoom)

    private fun calculateOrthoMatrix(projectionSize: Vector2f): Matrix4f {
        val projectionLeft = position.x - projectionSize.x / 2f
        val projectionRight = position.x + projectionSize.x / 2f
        val projectionTop = position.y - projectionSize.y / 2f
        val projectionBottom = position.y + projectionSize.y / 2f

        return Matrix4f().ortho(
            projectionLeft,
            projectionRight,
            projectionBottom,
            projectionTop,
            ProjectionOrthoZNear,
            ProjectionOrthoZFar
        )
    }

    companion object {
        private const val ProjectionOrthoZNear = 0.01f
        private const val ProjectionOrthoZFar = 100f
    }
}
