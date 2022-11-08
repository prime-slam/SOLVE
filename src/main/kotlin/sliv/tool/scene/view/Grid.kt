package sliv.tool.scene.view

import javafx.scene.Node

interface Grid {
    fun setUpPanning()
    fun getNode(): Node
}