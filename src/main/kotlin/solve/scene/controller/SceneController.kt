package solve.scene.controller

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import solve.scene.model.Scene
import solve.scene.view.DelayedFrameUpdatesManager
import solve.scene.view.SceneView
import solve.utils.ceilToInt
import solve.utils.structures.DoublePoint
import tornadofx.Controller
import tornadofx.onChange
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class SceneController : Controller() {
    private val view: SceneView by inject()

    val sceneWidthProperty = SimpleDoubleProperty()
    val sceneProperty = SimpleObjectProperty(Scene(emptyList(), emptyList()))
    val scene: Scene
        get() = sceneProperty.value

    val columnsNumberProperty = SimpleObjectProperty(calculateColumnsCount(scene))
    var columnsNumber: Int
        get() = columnsNumberProperty.value
        set(value) {
            if (value <= 0 || value > MaxColumnsNumber) {
                println("Number of the grid columns is out of range!")
                return
            }

            columnsNumberProperty.value = value
        }

    val scaleProperty = SimpleDoubleProperty(calculateScaleCorrespondingToColumns())
    var scale: Double
        get() = scaleProperty.value
        set(value) {
            scaleProperty.value = value
        }

    private val scaleLowValueProperty = SimpleDoubleProperty(MinScale)
    var scaleLowValue: Double
        get() = scaleLowValueProperty.value
        set(value) {
            if (value <= 0 || value >= scaleHighValue) {
                println("Min scene scale value should be a positive number that is less than max scene scale!")
                return
            }

            scaleLowValueProperty.value = value
        }

    private val scaleHighValueProperty = SimpleDoubleProperty(MaxScale)
    var scaleHighValue: Double
        get() = scaleHighValueProperty.value
        set(value) {
            if (value <= 0 || value <= scaleLowValue) {
                println("Max scene scale value should be a positive number that is greater than min scene scale!")
                return
            }

            scaleHighValueProperty.value = value
        }

    val xProperty = SimpleDoubleProperty(DefaultX)
    val yProperty = SimpleDoubleProperty(DefaultY)

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

    init {
        addGridSettingsBindings()
    }

    fun setScene(newScene: Scene, keepSettings: Boolean) {
        setDefaultScaleRange()
        columnsNumber = calculateColumnsCount(newScene)
        sceneProperty.value = newScene

        if (!keepSettings) {
            scale = calculateScaleCorrespondingToColumns()
        }
    }

    fun zoomIn(mousePosition: DoublePoint) = zoom(min(scale * ScaleFactor, scaleHighValue), mousePosition)

    fun zoomOut(mousePosition: DoublePoint) = zoom(max(scale / ScaleFactor, min(scaleLowValue, scale)), mousePosition)

    private fun zoom(newScale: Double, mousePosition: DoublePoint) {
        val initialMouseX = (xProperty.value + mousePosition.x) / scale
        val initialMouseY = (yProperty.value + mousePosition.y) / scale

        DelayedFrameUpdatesManager.doLockedAction {
            scaleProperty.value = newScale

            x = initialMouseX * scaleProperty.value - mousePosition.x
            y = initialMouseY * scaleProperty.value - mousePosition.y
        }
    }

    private fun addGridSettingsBindings() {
        columnsNumberProperty.onChange { newColumnsNumber ->
            newColumnsNumber ?: return@onChange

            view.currentGrid?.changeColumnsNumber(newColumnsNumber)
        }

        scaleLowValueProperty.onChange { newMinScale ->
            if (newMinScale > scale) {
                scale = newMinScale
            }
        }

        scaleHighValueProperty.onChange { newMaxScale ->
            if (newMaxScale < scale) {
                scale = newMaxScale
            }
        }
    }

    private fun calculateScaleCorrespondingToColumns() = max(
        MinScale,
        sceneWidthProperty.value / ((scene.frameSize.width + SceneView.framesMargin) * columnsNumber)
    )

    private fun setDefaultScaleRange() {
        scaleLowValue = MinScale
        scaleHighValue = MaxScale
    }

    companion object {
        const val MinScale = 0.2
        const val MaxScale = 10.0

        const val MaxColumnsNumber = 5

        private const val DefaultX = 0.0
        private const val DefaultY = 0.0

        private const val ScaleFactor = 1.15

        fun calculateColumnsCount(scene: Scene): Int {
            return min(sqrt(scene.frames.size.toDouble()).ceilToInt(), MaxColumnsNumber)
        }
    }
}
