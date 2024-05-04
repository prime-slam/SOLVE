package solve.scene.view.association

import javafx.animation.AnimationTimer
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import solve.styles.Style
import solve.utils.structures.DoublePoint
import tornadofx.*

/**
 * Creates adorner, which covers frame when it is selected to associate with another.
 * Manages size and position of the adorner to keep it filling the frame if scale changed.
 */
class AssociationAdorner(
    private val width: Double,
    private val height: Double,
    private val getFrameScreenPosition: () -> DoublePoint,
    private val getScale: () -> Double
) : AnimationTimer() {
    private val pane = StackPane()

    private val scale: Double
        get() = getScale()

    private val frameScreenPosition: DoublePoint
        get() = getFrameScreenPosition()

    init {
        start()
    }

    /**
     * Adorner visual node.
     */
    val node: Node = pane

    private val rectangle = Rectangle()

    private fun updateView() {
        pane.prefWidth = width * scale
        pane.prefHeight = height * scale
        pane.layoutX = frameScreenPosition.x * scale
        pane.layoutY = frameScreenPosition.y * scale
        rectangle.width = width * scale
        rectangle.height = height * scale
    }

    init {
        updateView()
        rectangle.fill = Paint.valueOf(Style.primaryColor)
        rectangle.opacity = RectangleOpacity

        val label = Label("Select second frame").also {
            it.textFill = Paint.valueOf(Style.surfaceColor)
            it.font = Font.font(Style.fontCondensed, FontWeight.BOLD, 18.0)
        }

        pane.add(rectangle)
        pane.add(label)
    }

    override fun handle(currentTime: Long) {
        updateView()
    }

    fun destroy() {
        stop()
    }

    companion object {
        private const val RectangleOpacity = 0.5
    }
}
