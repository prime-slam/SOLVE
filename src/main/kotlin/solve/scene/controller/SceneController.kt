package solve.scene.controller

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import solve.scene.model.Scene
import solve.scene.view.SceneView
import solve.utils.ceilToInt
import solve.utils.structures.DoublePoint as DoublePoint
import tornadofx.Controller
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class SceneController : Controller() {
    val sceneWidthProperty = SimpleDoubleProperty()
    val sceneProperty = SimpleObjectProperty(Scene(emptyList(), emptyList()))
    val scene: Scene
        get() = sceneProperty.value

    var columnsCount = calculateColumnsCount(scene)
        private set

    val scaleProperty = SimpleDoubleProperty(defaultScale)

    private val minScale
        get() = max(
            defaultMinScale, sceneWidthProperty.value / ((scene.frameSize.width + SceneView.framesMargin) * columnsCount)
        )

    val xProperty = SimpleDoubleProperty(defaultX)
    val yProperty = SimpleDoubleProperty(defaultY)

    // Scroll grid methods, controller's x and y properties can't be explicitly bound to grid's x and y properties
    // because grid can't scroll in certain cases
    var scrollX: ((Double) -> Double)? = null
    var scrollY: ((Double) -> Double)? = null

    var x: Double
        get() = xProperty.value
        private set(value) {
            val scrollX = scrollX ?: throw RuntimeException("scrollX method is not set")
            scrollX(value)
        }

    var y: Double
        get() = yProperty.value
        private set(value) {
            val scrollY = scrollY ?: throw RuntimeException("scrollY method is not set")
            scrollY(value)
        }

    fun setScene(newScene: Scene, keepSettings: Boolean) {
        columnsCount = calculateColumnsCount(newScene)
        sceneProperty.value = newScene
        if (!keepSettings || scaleProperty.value < minScale) {
            scaleProperty.value = minScale
        }
    }

    fun zoomIn(mousePosition: DoublePoint) = zoom(min(scaleProperty.value * scaleFactor, maxScale), mousePosition)

    fun zoomOut(mousePosition: DoublePoint) = zoom(max(scaleProperty.value / scaleFactor, min(minScale, scaleProperty.value)), mousePosition)

    private fun zoom(newScale: Double, mousePosition: DoublePoint) {
        val initialMouseX = (xProperty.value + mousePosition.x) / scaleProperty.value
        val initialMouseY = (yProperty.value + mousePosition.y) / scaleProperty.value

        scaleProperty.value = newScale

        x = initialMouseX * scaleProperty.value - mousePosition.x
        y = initialMouseY * scaleProperty.value - mousePosition.y
    }

    companion object {
        private const val defaultX = 0.0
        private const val defaultY = 0.0

        private const val defaultScale = 1.0
        private const val scaleFactor = 1.15
        private const val maxScale = 20.0
        private const val defaultMinScale = 0.2

        private const val maxColumnsCount = 5

        private fun calculateColumnsCount(scene: Scene): Int {
            return min(sqrt(scene.frames.size.toDouble()).ceilToInt(), maxColumnsCount)
        }
    }
}
