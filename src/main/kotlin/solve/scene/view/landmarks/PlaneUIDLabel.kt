package solve.scene.view.landmarks

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.WeakChangeListener
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import solve.scene.model.Landmark
import solve.utils.clearChildren
import solve.utils.getBlackOrWhiteContrastingTo
import solve.utils.structures.DoublePoint
import tornadofx.ChangeListener
import tornadofx.add

class PlaneUIDLabel(private val plane: Landmark.Plane) {
    val uidLabelNode = Group().also {  it.isMouseTransparent = true }
    private val uidLabel: Label by lazy {
        val createdLabel = buildUIDLabel()
        uidLabelNode.add(createdLabel)

        return@lazy createdLabel
    }

    var isShowingLabel: Boolean = false
        private set

    val enabledProperty = SimpleObjectProperty(true)
    var enabled: Boolean
        get() = enabledProperty.value
        set(value) {
            enabledProperty.value = value
        }

    private val planeCenterPoint: DoublePoint by lazy { calculatePlaneCenterPoint() }

    private val enabledChangedEventHandler = ChangeListener { _, _, nowEnabled ->
        if (nowEnabled) {
            uidLabelNode.add(uidLabel)
        } else {
            uidLabelNode.clearChildren()
        }
    }
    private val weakEnabledChangedEventHandler = WeakChangeListener(enabledChangedEventHandler)

    private fun calculatePlaneCenterPoint(): DoublePoint {
        val centroidPoint = DoublePoint(plane.points.map { it.x }.average(), plane.points.map { it.y }.average())
        val nearestToCentroid = plane.points.minBy { point ->
            DoublePoint(point.x.toDouble(), point.y.toDouble()).distanceTo(centroidPoint)
        }

        return DoublePoint(nearestToCentroid.x.toDouble(), nearestToCentroid.y.toDouble())
    }

    private fun buildUIDLabel(): Label {
        val uidLabel = Label(plane.uid.toString())
        uidLabel.textFill = getBlackOrWhiteContrastingTo(plane.layerSettings.getColor(plane))
        uidLabel.font = Font.font(null, FontWeight.BOLD, UIDLabelFontSize)
        uidLabel.isVisible = false

        return uidLabel
    }

    init {
        addListeners()
    }

    fun show(scale: Double) {
        if (isShowingLabel) {
            return
        }

        uidLabel.isVisible = false

        // Without delay the spawn coordinates of the uid label are incorrect.
        val uidSpawnCoroutineScope = CoroutineScope(Dispatchers.JavaFx)
        uidSpawnCoroutineScope.launch {
            delay(PlaneUIDLabelSpawnDelayMillis)
            updatePosition(scale)
            uidLabel.isVisible = true
            isShowingLabel = true
        }
    }

    fun hide() {
        if (!isShowingLabel) {
            return
        }

        uidLabel.isVisible = false
        isShowingLabel = false
    }

    fun updatePosition(scale: Double) {
        val labelCoordinates = planeCenterPoint * scale
        uidLabelNode.layoutX = labelCoordinates.x - (uidLabel.width) / 2.0
        uidLabelNode.layoutY = labelCoordinates.y - (uidLabel.height) / 2.0
    }

    private fun addListeners() {
        enabledProperty.addListener(weakEnabledChangedEventHandler)
    }

    companion object {
        private const val UIDLabelFontSize = 12.0

        private const val PlaneUIDLabelSpawnDelayMillis = 25L
    }
}