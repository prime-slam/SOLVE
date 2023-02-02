package solve.utils

import javafx.scene.Node
import tornadofx.add

fun Node.addAll(nodes: Collection<Node>) {
    nodes.forEach {
        add(it)
    }
}
