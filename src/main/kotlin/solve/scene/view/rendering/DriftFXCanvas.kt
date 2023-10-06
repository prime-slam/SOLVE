package solve.scene.view.rendering

import javafx.application.Platform
import org.eclipse.fx.drift.DriftFXSurface
import org.eclipse.fx.drift.GLRenderer
import org.eclipse.fx.drift.PresentationMode
import org.eclipse.fx.drift.Renderer
import org.eclipse.fx.drift.StandardTransferTypes
import org.eclipse.fx.drift.Swapchain
import org.eclipse.fx.drift.SwapchainConfig
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import solve.scene.view.rendering.ExampleRenderer.Companion.baseScene
import java.nio.ByteBuffer

class DriftFXCanvas(private val prefWidth: Double = 1000.0, private val prefHeight: Double = 1000.0) : TestCanvas() {
    val canvas = DriftFXSurface()

    private lateinit var renderer: Renderer

    private var glContextID: Long = 0L
    private var depthTextureID: Int = 0
    private var framebufferID: Int = 0
    private var swapchain: Swapchain? = null

    override fun initCanvas() {
        canvas.prefWidth = prefWidth
        canvas.prefHeight = prefHeight
        renderer = GLRenderer.getRenderer(canvas)

        startRenderLoop()
    }

    private fun startRenderLoop() {
        Platform.runLater {
            val thread = Thread(this::driftFXLoop)
            thread.isDaemon = true
            thread.start()
        }
    }

    private fun driftFXLoop() {
        try {
            glContextID = org.eclipse.fx.drift.internal.GL.createSharedCompatContext(0)
            org.eclipse.fx.drift.internal.GL.makeContextCurrent(glContextID)

            GL.createCapabilities()

            baseScene.init()
            baseScene.start()

            var timeMillis: Long = System.currentTimeMillis()
            var deltaTime = 0.1f
            var measurementNumber = 0

            val deltaTimes = mutableListOf<Float>()

            while (measurementNumber < InitialUnaccountedMeasurementsNumber + TestMeasurementsNumber) {
                val size = renderer.size

                if (swapchain == null || size.x != swapchain?.config?.size?.x || size.y != swapchain?.config?.size?.y) {
                    // re-create the swapchain
                    if (swapchain != null) {
                        swapchain?.dispose()
                    }
                    swapchain = renderer.createSwapchain(
                        SwapchainConfig(
                            size,
                            2,
                            PresentationMode.MAILBOX,
                            StandardTransferTypes.NVDXInterop
                        )
                    )
                }

                val currentSwapchain = swapchain

                if (currentSwapchain != null) {
                    val target = currentSwapchain.acquire()

                    val texId = GLRenderer.getGLTextureId(target)
                    texId.toString()

                    depthTextureID = GL11.glGenTextures()

                    // update depth tex
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureID)
                    GL11.glTexImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        GL30.GL_DEPTH_COMPONENT32F,
                        size.x,
                        size.y,
                        0,
                        GL11.GL_DEPTH_COMPONENT,
                        GL11.GL_FLOAT,
                        null as ByteBuffer?
                    )

                    framebufferID = GL30.glGenFramebuffers()
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferID)
                    GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texId, 0)
                    GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthTextureID, 0)

                    val status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
                    when (status) {
                        GL30.GL_FRAMEBUFFER_COMPLETE -> {}
                        GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> System.err.println("INCOMPLETE_ATTACHMENT!")
                    }

                    GL11.glViewport(0, 0, size.x, size.y)

                    GL30.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)

                    baseScene.update(deltaTime)

                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
                    GL30.glDeleteFramebuffers(framebufferID)
                    GL11.glDeleteTextures(depthTextureID)

                    currentSwapchain.present(target)
                }

                val currentTimeMillis = System.currentTimeMillis()
                deltaTime = (currentTimeMillis - timeMillis) / 1000f
                timeMillis = currentTimeMillis
                ++measurementNumber;
                deltaTimes.add(deltaTime)
            }

            println("Measurements number: $TestMeasurementsNumber")
            val averageDeltaTime = deltaTimes.subList(InitialUnaccountedMeasurementsNumber, deltaTimes.lastIndex).average()
            println("DriftFX average deltaTime: $averageDeltaTime")
            println("DriftFX average fps: ${1f / averageDeltaTime}")
        }
        catch (error: Exception) {
            println("DriftFX loop error!")
        }
    }
}