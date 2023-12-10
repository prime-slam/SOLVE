package solve.rendering.canvas

import javafx.application.Platform
import org.joml.Vector2f
import org.joml.Vector2i
import solve.rendering.engine.rendering.renderers.FramesRenderer
import solve.rendering.engine.scene.Scene
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.times
import solve.rendering.engine.utils.toFloatVector
import solve.scene.controller.SceneController
import solve.scene.model.VisualizationFrame
import solve.utils.ServiceLocator
import solve.utils.ceilToInt
import kotlin.io.path.Path
import kotlin.math.pow
import kotlin.math.sqrt

class OpenGLSceneCanvas : OpenGLCanvas(), TestCanvas {
    override val root = canvas

    private var sceneController: SceneController? = null
    private var framesRenderer: FramesRenderer? = null
    private var canvasScene: Scene? = null

    private var leftUpperCornerCameraPosition = Vector2f()
    private var rightLowerCornerCameraPosition = Vector2f()

    private var framesSelectionSize = 0
    private var framesSize = Vector2i()
    private var columnsNumber = 0
    private val rowsNumber: Int
        get() = (framesSelectionSize.toFloat() / columnsNumber.toFloat()).ceilToInt()

    private val measuredTimes = mutableListOf<Float>()
    private var measurementNumber = 0

    init {
        initializeCanvasEvents()
    }

    override fun drawFrames(testFramePath: String, scale: Float, gridWidth: Int, gridHeight: Int) {
        Platform.runLater {
            recalculateCameraCornersPositions()
            constraintCameraPosition()
        }

        val frame = VisualizationFrame(0L, Path(testFramePath), emptyList())
        framesRenderer?.setGridSize(gridWidth, gridHeight)
        framesRenderer?.setTestFrame(frame)
        window.camera.zoom = 2.6f * scale
    }


    override fun onInit() {
        val controller = ServiceLocator.getService<SceneController>() ?: return
        sceneController = controller

        val renderer = FramesRenderer(window)
        framesRenderer = renderer
        canvasScene = Scene(listOf(renderer))
    }

    override fun onDraw(deltaTime: Float) {
        measurementNumber += 1

        if (measurementNumber > TestCanvas.FirstSkippedMeasurementsNumber)
            measuredTimes.add(deltaTime)

        if (measurementNumber == TestCanvas.FirstSkippedMeasurementsNumber + TestCanvas.MeasurementsNumber) {
            val avgTime = measuredTimes.average()
            val variance = measuredTimes.sumOf { (it - avgTime).pow(2) } / measuredTimes.count()
            val deviation = sqrt(variance)

            println("Average time: $avgTime")
            println("Deviation: $deviation")
        }

        canvasScene?.renderers?.forEach { it.render() }
    }

    private fun constraintCameraPosition() {
        window.camera.position.x =
            window.camera.position.x.coerceIn(leftUpperCornerCameraPosition.x, rightLowerCornerCameraPosition.x)
        window.camera.position.y =
            window.camera.position.y.coerceIn(leftUpperCornerCameraPosition.y, rightLowerCornerCameraPosition.y)
    }

    private fun recalculateCameraCornersPositions() {
        val halfScreenSize = (Vector2f(window.width.toFloat(), window.height.toFloat()) / 2f) / window.camera.scaledZoom
        leftUpperCornerCameraPosition = halfScreenSize

        val framesSelectionSize =
            Vector2i(columnsNumber * framesSize.x, rowsNumber * framesSize.y).toFloatVector()
        val framesSelectionScreenSize =
            framesSelectionSize * window.camera.zoom / IdentityFramesSizeScale / window.camera.scaledZoom

        rightLowerCornerCameraPosition = framesSelectionScreenSize - leftUpperCornerCameraPosition

        rightLowerCornerCameraPosition.x =
            rightLowerCornerCameraPosition.x.coerceAtLeast(leftUpperCornerCameraPosition.x)
        rightLowerCornerCameraPosition.y =
            rightLowerCornerCameraPosition.y.coerceAtLeast(leftUpperCornerCameraPosition.y)
    }

    companion object {
        const val IdentityFramesSizeScale = 1.605f
    }
}
