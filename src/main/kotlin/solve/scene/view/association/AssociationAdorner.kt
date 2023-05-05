package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Region
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
    private val framePosition: DoublePoint,
    private val scale: DoubleProperty
) {
    private val pane = StackPane()

    /**
     * Adorner visual node.
     */
    val node: Node = pane

    init {
        pane.prefWidth = width * scale.value
        pane.prefHeight = height * scale.value
        pane.layoutX = framePosition.x * scale.value
        pane.layoutY = framePosition.y * scale.value

        val rect = Rectangle()
        rect.width = width * scale.value
        rect.height = height * scale.value
        rect.fill = Paint.valueOf(Style.primaryColor)
        rect.opacity = RectangleOpacity

        val label = Label("Select second frame").also {
            it.textFill = Paint.valueOf(Style.surfaceColor)
            it.font = Font.font(Style.fontCondensed, FontWeight.BOLD, 20.0)
            it.minWidth = Region.USE_PREF_SIZE
        }

        pane.add(rect)
        pane.add(label)

        scale.onChange {
            pane.prefWidth = width * scale.value
            pane.prefHeight = height * scale.value
            pane.layoutX = framePosition.x * scale.value
            pane.layoutY = framePosition.y * scale.value
            rect.width = width * scale.value
            rect.height = height * scale.value
        }
    }

    companion object {
        private const val RectangleOpacity = 0.5
    }
}
