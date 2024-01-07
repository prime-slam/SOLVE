package solve.unit.rendering.utils

import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import org.lwjgl.opengl.GL.createCapabilities
import com.huskerdev.openglfx.OpenGLCanvas as OpenGLFXCanvas

internal fun runInOpenGLContext(action: () -> Unit) {
    val openGLCanvas = OpenGLFXCanvas.create(LWJGLExecutor.LWJGL_MODULE)
    openGLCanvas.addOnInitEvent {
        createCapabilities()
        action()
    }
}
