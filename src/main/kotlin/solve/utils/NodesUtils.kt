package solve.utils

import javafx.geometry.Bounds
import javafx.scene.Node
import solve.utils.structures.Point
import tornadofx.*

fun Node.addAll(nodes: Collection<Node>) {
    nodes.forEach {
        add(it)
    }
}

fun Node.clearChildren() {
    getChildList()?.clear()
}

fun Node.removeSafely(node: Node?) {
    node.let { getChildList()?.remove(it) }
}

fun Node.addSafely(node: Node?) {
    node?.let { add(it) }
}


fun Node.getSceneBounds(): Bounds = localToScene(boundsInLocal)

fun Node.getScenePosition(): Point {
    val bounds = getSceneBounds()

    return Point(bounds.minX, bounds.minY)
}

fun Node.getSceneCenterPosition(): Point {
    val bounds = getSceneBounds()

    return Point(bounds.centerX, bounds.centerY)
}

fun Node.getSceneMaxPosition(): Point {
    val bounds = getSceneBounds()

    return Point(bounds.maxX, bounds.maxY)
}
