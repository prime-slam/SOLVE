package solve.unit.rendering.utils

import com.huskerdev.openglfx.OpenGLCanvas as OpenGLFXCanvas
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import org.lwjgl.opengl.GL.createCapabilities

internal inline fun runInOpenGLContext(crossinline action: () -> Unit) {
    val openGLCanvas = OpenGLFXCanvas.create(LWJGLExecutor.LWJGL_MODULE)
    openGLCanvas.addOnInitEvent {
        createCapabilities()
        action()
    }
}