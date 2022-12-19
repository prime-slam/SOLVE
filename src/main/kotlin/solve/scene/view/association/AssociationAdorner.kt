package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class AssociationAdorner(private val width: Double, private val height: Double, private val scale: DoubleProperty) {
    private val pane = StackPane()
    val node: Node = pane

    init {
        pane.prefWidth = width * scale.value
        pane.prefHeight = height * scale.value

        val rect = Rectangle()
        rect.width = width * scale.value
        rect.height = height * scale.value
        rect.fill = RectangleColor
        rect.opacity = RectangleOpacity

        val label = Label("Select second frame")
        label.background = Background(BackgroundFill(Color.WHITE,
            CornerRadii.EMPTY, Insets.EMPTY))

        pane.add(rect)
        pane.add(label)

        scale.onChange {
            pane.prefWidth = width * scale.value
            pane.prefHeight = height * scale.value
            rect.width = width * scale.value
            rect.height = height * scale.value
        }
    }

    companion object {
        private const val RectangleOpacity = 0.3
        private val RectangleColor: Color = Color.BLUE
    }
}