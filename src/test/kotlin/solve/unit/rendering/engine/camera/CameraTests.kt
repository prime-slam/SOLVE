package solve.unit.rendering.engine.camera

import org.joml.Matrix4f
import org.joml.Vector2f
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import solve.rendering.engine.camera.Camera

internal class CameraTests {
    @Test
    fun `Zooms to center and changes camera zoom`() {
        val camera = Camera()
        val zoomMultiplier = 2.5f
        assertEquals(1f, camera.zoom)
        camera.zoomToCenter(zoomMultiplier)
        assertEquals(zoomMultiplier, camera.zoom)
    }
    @Test
    fun `Calculates camera projection matrix`() {
        val camera = Camera()
        val projectionSize = Vector2f(100f, 50f)
        camera.zoomToCenter(2f)
        assertEquals(
            Matrix4f(
                12f, 0f, 0f, 0f,
                0f, 24f, 0f, 0f,
                0f, 0f, -6f, 0f,
                0f, 0f, 0f, 1f
            ),
            camera.calculateProjectionMatrix(projectionSize)
        )
    }
}