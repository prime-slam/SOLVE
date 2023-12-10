package solve.scene.view

import solve.rendering.canvas.BufferedImageCanvas
import solve.rendering.canvas.BuiltInCanvas
import solve.rendering.canvas.OpenGLSceneCanvas
import solve.rendering.canvas.TestCanvas
import solve.rendering.canvas.WritableImageCanvas
import solve.utils.getResourceAbsolutePath
import tornadofx.View

/**
 * Scene visual component, represents grid with frames.
 * Set up global scene properties.
 */
class SceneView : View() {
    // Assign one of the test canvases.
    private val canvas: TestCanvas = BuiltInCanvas()

    override val root = canvas.root

    fun initialize() {
        canvas.drawFrames(
            getResourceAbsolutePath("test_image.jpg").toString(),
            0.15f,
            15, 15
        )
    }

    companion object {
        const val framesMargin = 0.0
    }
}
