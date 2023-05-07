package solve.scene.view

import javafx.beans.property.DoubleProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.ScrollEvent

/**
 * Represents frames, responsible for layouting and virtualized rendering and data loading.
 */
interface Grid {
    /**
     * Initializes navigation on scene with panning.
     */
    fun setUpPanning()

    /**
     * Visual node.
     */
    val node: Node

    /**
     * Current scrolling position.
     */
    val xProperty: DoubleProperty
    val yProperty: DoubleProperty

    /**
     * Scrolls grid, if border position is reached, returns max value.
     */
    fun scrollX(newX: Double): Double
    fun scrollY(newY: Double): Double

    /**
     * Adds event handler on mouse scrolling on grid. Used for scrolling.
     */
    fun setOnMouseWheel(handler: EventHandler<ScrollEvent>)

    /**
     * Recalculates frames positions with new columns number.
     * Empty space is filled with empty space.
     */
    fun changeColumnsNumber(columnsNumber: Int)

    fun dispose()
}
