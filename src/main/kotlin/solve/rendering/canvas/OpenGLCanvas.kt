package solve.rendering.canvas

import com.huskerdev.openglfx.GLCanvasAnimator
import com.huskerdev.openglfx.events.GLInitializeEvent
import com.huskerdev.openglfx.events.GLRenderEvent
import com.huskerdev.openglfx.events.GLReshapeEvent
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import org.joml.Vector2f
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
import solve.project.model.ProjectFrame
import solve.rendering.engine.Window
import solve.rendering.engine.camera.Camera
import solve.rendering.engine.rendering.renderers.FramesRenderer
import solve.rendering.engine.scene.Scene
import solve.utils.getResourceAbsolutePath
import java.nio.file.Path
import com.huskerdev.openglfx.OpenGLCanvas as OpenGLFXCanvas

class OpenGLCanvas {
    val canvas: OpenGLFXCanvas = OpenGLFXCanvas.create(LWJGLExecutor.LWJGL_MODULE)

    init {
        canvas.animator = GLCanvasAnimator(OpenGLCanvasFPS)

        canvas.addOnReshapeEvent(this::reshape)
        canvas.addOnRenderEvent(this::render)
        canvas.addOnInitEvent(this::canvasInit)
    }

    private lateinit var window: Window
    private lateinit var renderer: FramesRenderer

    var time = 0f

    /*private lateinit var shaderProgram: ShaderProgram
    private lateinit var camera: Camera
    private lateinit var vertices: FloatArray
    private lateinit var elements: IntArray
    private lateinit var texture: Texture
    private var vaoID = 0
    private var vboID = 0
    private var eboID = 0*/

    fun draw(deltaTime: Float) {
        println(1 / deltaTime)
        renderer.render()
        time += deltaTime

        /*shaderProgram.use()
        shaderProgram.uploadTexture("uTex", 0)

        glActiveTexture(GL_TEXTURE0)
        texture.bind()


        shaderProgram.uploadMatrix4f("uProjection", Matrix4f().identity().scale(0.001f))

        glBindVertexArray(vaoID)

        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)

        glDrawElements(GL_TRIANGLES, elements.size, GL_UNSIGNED_INT, 0)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glEnableVertexAttribArray(2)

        glBindVertexArray(0)

        shaderProgram.detach()*/
    }

    private fun canvasInit(event: GLInitializeEvent) {
        event.toString()
        createCapabilities()

        /*shaderProgram = ShaderProgram()
        shaderProgram.addShader(ShadersDefaultVertexPath, ShaderType.VERTEX)
        shaderProgram.addShader(ShadersDefaultFragmentPath, ShaderType.FRAGMENT)
        shaderProgram.link()

        camera = Camera()

        vertices = floatArrayOf(
            100f, 0f, 0f,                1f, 0f, 0f, 1f,   1f, 1f,
            0f, 100f, 0f,                0f, 1f, 0f, 1f,   0f, 0f,
            100f, 100f, 0f,              1f, 0f, 1f, 1f,   1f, 0f,
            0f, 0f, 0f,                  1f, 1f, 0f, 1f,   0f, 1f
        )

        elements = intArrayOf(2, 1, 0, 0, 1, 3)
        vaoID = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vaoID)

        val verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
        verticesBuffer.put(vertices).flip()

        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW)

        val elementsBuffer = BufferUtils.createIntBuffer(elements.size)
        elementsBuffer.put(elements).flip()

        val eboID = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementsBuffer, GL_STATIC_DRAW)

        val posSize = 3
        val colorSize = 4
        val uvSize = 2
        val verticesSizeBytes = (posSize + colorSize + uvSize) * Float.SIZE_BYTES
        GL20C.glVertexAttribPointer(0, posSize, GL_FLOAT, false, verticesSizeBytes, 0L)
        GL20C.glEnableVertexAttribArray(0)

        GL20C.glVertexAttribPointer(1, colorSize, GL_FLOAT, false, verticesSizeBytes, (posSize * Float.SIZE_BYTES).toLong())
        GL20C.glEnableVertexAttribArray(1)

        GL20C.glVertexAttribPointer(2, uvSize, GL_FLOAT, false, verticesSizeBytes, ((posSize + colorSize) * Float.SIZE_BYTES).toLong())
        GL20C.glEnableVertexAttribArray(2)

        texture = Texture("icons/img.png")*/

        window = Window(1920, 600, Camera(Vector2f(0f, 0f), 5f))
        val scene = Scene()
        window.changeScene(scene)
        val frames = List<ProjectFrame>(100000) {
            ProjectFrame(
                0L,
                Path.of(getResourceAbsolutePath("icons/img.png")!!),
                emptyList()
            )
        }
        renderer = FramesRenderer(window)
        renderer.setSceneFrames(frames)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun render(event: GLRenderEvent) {
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        draw(event.delta.toFloat())
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
