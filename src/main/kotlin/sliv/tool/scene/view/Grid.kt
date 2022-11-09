package sliv.tool.scene.view

import javafx.scene.Node

interface Grid {
    fun setUpPanning()
    fun getNode(): Node

    fun scrollTo(x: Double, y: Double)

    fun scrollBy(x: Double, y: Double)

    fun getPosition(): Pair<Double, Double>

    fun getSize(): Pair<Double, Double>
}