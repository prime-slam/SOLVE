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

    fun scrollX(newX: Double): Double
    fun scrollY(newY: Double): Double

    fun setOnMouseWheel(handler: EventHandler<ScrollEvent>)

    fun changeColumnsNumber(columnsNumber: Int)

    fun dispose()
}
