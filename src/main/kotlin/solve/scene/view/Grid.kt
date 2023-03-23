package solve.scene.view

import javafx.beans.property.DoubleProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.ScrollEvent

interface Grid {
    fun setUpPanning()

    val node: Node

    val xProperty: DoubleProperty
    val yProperty: DoubleProperty

    fun setOnScroll(handler: EventHandler<ScrollEvent>)

    fun dispose()
}