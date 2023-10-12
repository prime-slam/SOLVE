package solve.rendering.canvas

import com.huskerdev.openglfx.GLCanvasAnimator
import com.huskerdev.openglfx.events.GLInitializeEvent
import com.huskerdev.openglfx.events.GLRenderEvent
import com.huskerdev.openglfx.events.GLReshapeEvent
import com.huskerdev.openglfx.OpenGLCanvas as OpenGLFXCanvas
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11

class OpenGLCanvas {
    lateinit var canvas: OpenGLFXCanvas

    init {
        canvas = OpenGLFXCanvas.create(LWJGLExecutor.LWJGL_MODULE)
        canvas.animator = GLCanvasAnimator(OpenGLCanvasFPS)

        canvas.addOnReshapeEvent(this::reshape)
        canvas.addOnRenderEvent(this::render)
        canvas.addOnInitEvent(this::canvasInit)
    }

    fun draw() {

    }

    private fun canvasInit(event: GLInitializeEvent) {
        event.toString()
        GL.createCapabilities()
    }

    private fun render(event: GLRenderEvent) {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthFunc(GL11.GL_LEQUAL)

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        draw()
    }

    private fun reshape(event: GLReshapeEvent){
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glLoadIdentity()

        val aspect = event.height.toDouble() / event.width
        GL11.glFrustum(-1.0, 1.0, -aspect, aspect, 5.0, 60.0)
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadIdentity()

        GL11.glTranslatef(0.0f, 0.0f, -40.0f)
    }

    companion object {
        private const val OpenGLCanvasFPS = 60.0
    }
}