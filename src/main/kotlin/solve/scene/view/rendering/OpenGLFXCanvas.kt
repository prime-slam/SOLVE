package solve.scene.view.rendering

import com.huskerdev.openglfx.GLCanvasAnimator
import com.huskerdev.openglfx.OpenGLCanvas
import com.huskerdev.openglfx.events.GLInitializeEvent
import com.huskerdev.openglfx.events.GLRenderEvent
import com.huskerdev.openglfx.events.GLReshapeEvent
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import solve.scene.view.rendering.engine.jade.BaseScene

class OpenGLFXCanvas(private val prefWidth: Double = 1000.0, private val prefHeight: Double = 1000.0) : TestCanvas() {
    lateinit var canvas: OpenGLCanvas

    private val deltaTimes = mutableListOf<Double>()
    private var measurementsNumber = 0

    private lateinit var baseScene: BaseScene

    private var measured = false


    override fun initCanvas() {
        baseScene = BaseScene()

        canvas = OpenGLCanvas.create(LWJGLExecutor.LWJGL_MODULE)
        canvas.animator = GLCanvasAnimator(200.0)
        canvas.prefHeight = prefHeight
        canvas.prefWidth = prefWidth

        canvas.addOnReshapeEvent(this::reshape)
        canvas.addOnRenderEvent(this::render)
        canvas.addOnInitEvent(this::canvasInit)    }

    private fun canvasInit(event: GLInitializeEvent) {
        event.toString()
        GL.createCapabilities()
        baseScene.init()
        baseScene.start()
    }

    private fun render(event: GLRenderEvent) {
        if (measurementsNumber < InitialUnaccountedMeasurementsNumber + TestMeasurementsNumber) {
            deltaTimes.add(event.delta)

            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDepthFunc(GL11.GL_LEQUAL)

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            baseScene.update(event.delta.toFloat())

            ++measurementsNumber
        } else if (!measured) {
            println("Measurements number: $TestMeasurementsNumber")
            val averageDeltaTime =
                deltaTimes.subList(InitialUnaccountedMeasurementsNumber, deltaTimes.lastIndex).average()
            println("OpenGLFX average deltaTime: $averageDeltaTime")
            println("OpenGLFX average fps: ${1f / averageDeltaTime}")

            measured = true
        }
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
}