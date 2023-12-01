package solve.scene.controller

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import solve.rendering.canvas.SceneCanvas
import solve.scene.model.Scene
import solve.scene.view.SceneView
import solve.utils.ServiceLocator
import solve.utils.ceilToInt
import tornadofx.Controller
import tornadofx.onChange
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Manages scene data, responds for interaction of scene with another components of application.
 * Allows settings component to edit scale range and columns number properties of the scene.
 */
class SceneController : Controller() {
    init {
        ServiceLocator.registerService(this)
    }

    /**
     * Real width of the scene visual component.
     * Used to recalculate scale and columns number.
     */
    val sceneWidthProperty = SimpleDoubleProperty()

    /**
     * Scene data object.
     */
    val sceneProperty = SimpleObjectProperty(Scene(emptyList(), emptyList()))
    val scene: Scene
        get() = sceneProperty.value

    /**
     * Installed in the settings columns number.
     */
    val installedColumnsNumberProperty = SimpleIntegerProperty(0)
    var installedColumnsNumber: Int
        get() = installedColumnsNumberProperty.value
        set(value) {
            if (value <= 0 || value > MaxColumnsNumber) {
                println("Number of the grid columns is out of range!")
                return
            }

            installedColumnsNumberProperty.value = value
        }

    /**
     * Real used columns number, by default calculated depending on frames count.
     */
    val columnsNumber: Int
        get() = min(scene.frames.count(), installedColumnsNumber)

    /**
     * Editable low border of scene scale range.
     */
    val installedMinScaleProperty = SimpleDoubleProperty(DefaultMinScale)
    var installedMinScale: Double
        get() = installedMinScaleProperty.value
        set(value) {
            if (value <= 0 || value >= installedMaxScale) {
                println("Min scene scale value should be a positive number that is less than max scene scale!")
                return
            }

            installedMinScaleProperty.value = value
        }

    /**
     * Editable top border of scene scale range.
     */
    val installedMaxScaleProperty = SimpleDoubleProperty(DefaultMaxScale)
    var installedMaxScale: Double
        get() = installedMaxScaleProperty.value
        set(value) {
            if (value <= 0 || value <= installedMinScale) {
                println("Max scene scale value should be a positive number that is greater than min scene scale!")
                return
            }

            installedMaxScaleProperty.value = value
        }

    /**
     * Current scale value, used in all elements of the scene,
     * which size or position depends on scale (frames, landmarks, associations, etc.)
     */
    val scaleProperty = SimpleDoubleProperty(calculateMinScaleDependingOnColumns())
    var scale: Double
        get() = scaleProperty.value
        private set(value) {
            scaleProperty.value = value.coerceIn(
                max(installedMinScale, minScale),
                min(installedMaxScale, maxScale)
            )
        }

    private val minScale: Double
        get() = calculateMinScaleDependingOnColumns()
    private val maxScale: Double
        get() = installedMaxScale

    val xProperty = SimpleDoubleProperty(DefaultX)
    val yProperty = SimpleDoubleProperty(DefaultY)

    /**
     * Grid scroll methods,  controller's x and y properties can't be explicitly bound to grid's x and y properties,
     * because grid can't scroll in certain cases
     */
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

    private val hasEmptySpace: Boolean
        get() = scale < calculateMinScaleDependingOnColumns()

    init {
        addGridSettingsBindings()
    }

    fun increaseScale() {
        scale *= ScaleFactor
    }

    fun decreaseScale() {
        scale /= ScaleFactor
    }

    /**
     * Changes scene data object and recalculates visual properties.
     */
    fun setScene(newScene: Scene, keepSettings: Boolean) {
        sceneProperty.value = newScene

        if (!keepSettings) {
            reinitializeSettings(newScene)
        }
        recalculateScale(false)
    }

    /**
     * Recalculates current scale value to avoid empty space.
     */
    fun recalculateScale(keepOldScale: Boolean) {
        if (!keepOldScale || hasEmptySpace) {
            scale = minScale
        }
    }

    private fun reinitializeSettings(newScene: Scene) {
        installedColumnsNumber = calculateColumnsNumber(newScene)
        setDefaultScaleRange()
    }

    private fun addGridSettingsBindings() {
        installedColumnsNumberProperty.onChange {
            scale = calculateMinScaleDependingOnColumns()
        }

        installedMinScaleProperty.onChange { installedMinScale ->
            if (installedMinScale > scale) {
                scale = installedMinScale
            }
        }

        installedMaxScaleProperty.onChange { installedMaxScale ->
            if (installedMaxScale < scale) {
                scale = installedMaxScale
            }
        }
    }

    /**
     * By default, scene tries to show frames on minimal possible scale
     * and avoids empty space.
     */
    private fun calculateMinScaleDependingOnColumns(): Double {
        return min(
            max(
                installedMinScale,
                sceneWidthProperty.value / ((scene.frameSize.width + SceneView.framesMargin) * columnsNumber) *
                    SceneCanvas.IdentityFramesSizeScale
            ),
            DefaultMaxScale
        )
    }

    private fun setDefaultScaleRange() {
        installedMaxScale = DefaultMaxScale
        installedMinScale = DefaultMinScale
    }

    companion object {
        const val DefaultMinScale = 0.2
        const val DefaultMaxScale = 20.0

        const val MaxColumnsNumber = 6

        private const val DefaultX = 0.0
        private const val DefaultY = 0.0

        private const val ScaleFactor = 1.15

        fun calculateColumnsNumber(scene: Scene): Int {
            return min(sqrt(scene.frames.size.toDouble()).ceilToInt(), MaxColumnsNumber)
        }
    }
}
