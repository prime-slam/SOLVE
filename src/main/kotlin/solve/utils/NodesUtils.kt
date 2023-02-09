package solve.utils

import javafx.scene.Node
import tornadofx.add
import tornadofx.getChildList

fun Node.addAll(nodes: Collection<Node>) {
    nodes.forEach {
        add(it)
    }
}

fun Node.clearChildren() {
    getChildList()?.clear()
}
