package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*

class AssociationAdorner(
    private val width: Double,
    private val height: Double,
    private val framePosition: Pair<Double, Double>,
    private val scale: DoubleProperty
) {
    private val pane = StackPane()
    val node: Node = pane

    init {
        pane.prefWidth = width * scale.value
        pane.prefHeight = height * scale.value
        pane.layoutX = framePosition.first * scale.value
        pane.layoutY = framePosition.second * scale.value

        val rect = Rectangle()
        rect.width = width * scale.value
        rect.height = height * scale.value
        rect.fill = RectangleColor
        rect.opacity = RectangleOpacity

        val label = Label("Select second frame").also {
            it.textFill = Paint.valueOf("#FFFFFF")
            it.font = Font.font("Roboto Condensed", FontWeight.BOLD, 20.0)
        }

        pane.add(rect)
        pane.add(label)

        scale.onChange {
            pane.prefWidth = width * scale.value
            pane.prefHeight = height * scale.value
            pane.layoutX = framePosition.first * scale.value
            pane.layoutY = framePosition.second * scale.value
            rect.width = width * scale.value
            rect.height = height * scale.value
        }
    }

    companion object {
        private const val RectangleOpacity = 0.5
        private val RectangleColor: Color = c("78909c")
    }
}
