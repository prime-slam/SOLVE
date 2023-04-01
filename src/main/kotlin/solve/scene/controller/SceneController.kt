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

class SceneController : Controller() {
    private val view: SceneView by inject()

    val sceneWidthProperty = SimpleDoubleProperty()
    val sceneProperty = SimpleObjectProperty(Scene(emptyList(), emptyList()))
    val scene: Scene
        get() = sceneProperty.value

    private val columnsNumberProperty = SimpleObjectProperty(DefaultColumnsNumber)
    var columnsNumber: Int
        get() = columnsNumberProperty.value
        set(value) {
            if (value <= 0 || value >= MaxColumnsNumber) {
                println("Number of the grid columns should be a positive number!")
                return
            }

            columnsNumberProperty.value = value
        }

    val scaleProperty = SimpleDoubleProperty(DefaultScale)
    var scale: Double
        get() = scaleProperty.value
        set(value) {
            scaleProperty.value = value
        }

    private val minScaleProperty = SimpleDoubleProperty(DefaultMinScale)
    var minScale: Double
        get() = minScaleProperty.value
        set(value) {
            if (value <= 0 || value >= maxScale) {
                println("Min scene scale value should be a positive number that is less than max scene scale!")
                return
            }

            minScaleProperty.value = value
        }

    private val maxScaleProperty = SimpleDoubleProperty(DefaultMaxScale)
    var maxScale: Double
        get() = maxScaleProperty.value
        set(value) {
            if (value <= 0 || value <= minScale) {
                println(value)
                println("Max scene scale value should be a positive number that is greater than min scene scale!")
                return
            }

            maxScaleProperty.value = value
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
        sceneProperty.value = newScene

        if (!keepSettings) {
            scale = minScale
        }
    }

    fun zoomIn(mousePosition: DoublePoint) = zoom(min(scale * ScaleFactor, maxScale), mousePosition)

    fun zoomOut(mousePosition: DoublePoint) = zoom(max(scale / ScaleFactor, min(minScale, scale)), mousePosition)

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

        minScaleProperty.onChange { newMinScale ->
            if (newMinScale > scale) {
                scale = newMinScale
            }
        }

        maxScaleProperty.onChange { newMaxScale ->
            if (newMaxScale < scale) {
                scale = newMaxScale
            }
        }
    }

    companion object {
        const val DefaultMinScale = 0.2
        const val DefaultMaxScale = 20.0

        const val DefaultColumnsNumber = 4
        const val MaxColumnsNumber = 10

        private const val DefaultX = 0.0
        private const val DefaultY = 0.0

        private const val DefaultScale = 1.0
        private const val ScaleFactor = 1.15
    }
}
