package sliv.tool.scene.view

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.ScrollEvent

interface Grid {
    fun setUpPanning()

    val node: Node

    fun setOnScroll(handler: EventHandler<ScrollEvent>)

    fun scrollTo(x: Double, y: Double)

    fun getPosition(): Pair<Double, Double>
}