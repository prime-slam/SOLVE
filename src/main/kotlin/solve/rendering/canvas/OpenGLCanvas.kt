package solve.rendering.canvas

import com.huskerdev.openglfx.GLCanvasAnimator
import com.huskerdev.openglfx.events.GLInitializeEvent
import com.huskerdev.openglfx.events.GLRenderEvent
import com.huskerdev.openglfx.events.GLReshapeEvent
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.GL_LEQUAL
import org.lwjgl.opengl.GL11.GL_PROJECTION
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glDepthFunc
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glFrustum
import org.lwjgl.opengl.GL11.glLoadIdentity
import org.lwjgl.opengl.GL11.glMatrixMode
import org.lwjgl.opengl.GL11.glTranslatef
import solve.rendering.engine.Window
import com.huskerdev.openglfx.OpenGLCanvas as OpenGLFXCanvas

abstract class OpenGLCanvas {
    val canvas: OpenGLFXCanvas = OpenGLFXCanvas.create(LWJGLExecutor.LWJGL_MODULE)

    protected lateinit var window: Window

    init {
        canvas.animator = GLCanvasAnimator(OpenGLCanvasFPS)
    }

    open fun onInit() { }

    open fun onDraw(deltaTime: Float) { }

    protected fun initializeCanvasEvents() {
        canvas.addOnReshapeEvent(this::reshape)
        canvas.addOnRenderEvent(this::render)
        canvas.addOnInitEvent(this::canvasInit)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun canvasInit(event: GLInitializeEvent) {
        window = Window(canvas.width.toInt(), canvas.height.toInt())
        println(canvas.width)
        println(canvas.height)
        createCapabilities()

        onInit()
    }

    private fun render(event: GLRenderEvent) {
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        onDraw(event.delta.toFloat())
    }

    private fun reshape(event: GLReshapeEvent) {
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()

        val aspect = event.height.toDouble() / event.width
        glFrustum(-1.0, 1.0, -aspect, aspect, 5.0, 60.0)
        glMatrixMode(GL11.GL_MODELVIEW)
        glLoadIdentity()

        glTranslatef(0.0f, 0.0f, -40.0f)
    }

    companion object {
        private const val OpenGLCanvasFPS = 60.0
    }
}
