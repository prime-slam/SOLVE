package solve.rendering.utils

import com.huskerdev.openglfx.canvas.GLCanvas
import com.huskerdev.openglfx.canvas.GLCanvasAnimator
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import org.lwjgl.opengl.GL.createCapabilities

internal fun runInOpenGLContext(action: () -> Unit) {
    val openGLCanvas = GLCanvas(LWJGLExecutor.LWJGL_MODULE)
    openGLCanvas.animator = GLCanvasAnimator(60.0)
    openGLCanvas.addOnInitEvent {
        createCapabilities()
    }
    var isActionRan = false
    openGLCanvas.addOnRenderEvent {
        if (isActionRan) {
            return@addOnRenderEvent
        }

        action()
    }
}
