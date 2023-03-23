package solve.scene.controller

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import solve.scene.model.Scene
import tornadofx.Controller
import kotlin.math.max
import kotlin.math.min

class SceneController : Controller() {
    val scene = SimpleObjectProperty(Scene(emptyList(), emptyList()))

    val scaleProperty = SimpleDoubleProperty(defaultScale)

    val xProperty = SimpleDoubleProperty(defaultX)
    val yProperty = SimpleDoubleProperty(defaultY)

    // Scroll grid methods, controller's x and y properties can't be explicitly bound to grid's x and y properties
    // because grid can't scroll in certain cases
    var scrollX: ((Double) -> Double)? = null
    var scrollY: ((Double) -> Double)? = null

    var x: Double
        get() = xProperty.value
        set(value) {
            val scrollX = scrollX ?: throw RuntimeException("scrollX method is not set")
            scrollX(value)
        }

    var y: Double
        get() = yProperty.value
        set(value) {
            val scrollY = scrollY ?: throw RuntimeException("scrollY method is not set")
            scrollY(value)
        }

    fun zoomIn(mousePosition: Pair<Double, Double>) {
        val initialMouseX = (xProperty.value + mousePosition.first) / scaleProperty.value
        val initialMouseY = (yProperty.value + mousePosition.second) / scaleProperty.value

        scaleProperty.value = min(scaleProperty.value * scaleFactor, maxScale)

        x = initialMouseX * scaleProperty.value - mousePosition.first
        y = initialMouseY * scaleProperty.value - mousePosition.second
    }

    fun zoomOut(mousePosition: Pair<Double, Double>) {
        val initialMouseX = (xProperty.value + mousePosition.first) / scaleProperty.value
        val initialMouseY = (yProperty.value + mousePosition.second) / scaleProperty.value

        scaleProperty.value = max(scaleProperty.value / scaleFactor, minScale)

        x = initialMouseX * scaleProperty.value - mousePosition.first
        y = initialMouseY * scaleProperty.value - mousePosition.second
    }

    companion object {
        private const val defaultX = 0.0
        private const val defaultY = 0.0
        private const val defaultScale = 1.0
        private const val scaleFactor = 1.15
        private const val maxScale = 20.0
        private const val minScale = 0.2
    }
}