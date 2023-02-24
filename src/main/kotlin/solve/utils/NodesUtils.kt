package solve.utils

import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import solve.catalogue.floor
import solve.utils.structures.Point
import tornadofx.*
import kotlin.math.min

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

fun Node.getScreenBounds(): Bounds = localToScreen(boundsInLocal)

fun Node.getScenePosition(): Point {
    val bounds = getSceneBounds()

    return Point(bounds.minX, bounds.minY)
}

fun Node.getScreenPosition(): Point {
    val bounds = getScreenBounds()

    return Point(bounds.minX, bounds.minY)
}

fun Node.createSnapshot(): Image {
    val nodeSnapshot = snapshot(null, null)
    return WritableImage(
        nodeSnapshot.pixelReader,
        nodeSnapshot.width.floor(),
        nodeSnapshot.height.floor()
    )
}
