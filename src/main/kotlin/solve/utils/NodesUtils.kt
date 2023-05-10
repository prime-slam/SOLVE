package solve.utils

import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import solve.utils.structures.DoublePoint
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

fun Node.getScreenBounds(): Bounds = localToScreen(boundsInLocal) ?: BoundingBox(1.0, 1.0, 1.0, 1.0)

fun Node.getScreenPosition(): DoublePoint {
    val bounds = getScreenBounds()

    return DoublePoint(bounds.minX, bounds.minY)
}

fun Node.createSnapshot(): Image {
    val nodeSnapshot = snapshot(null, null)
    return WritableImage(
        nodeSnapshot.pixelReader,
        nodeSnapshot.width.floorToInt(),
        nodeSnapshot.height.floorToInt()
    )
}

fun Node.scale(value: Double) {
    if (value <= 0) {
        println("Scale value should be a positive number!")
        return
    }

    scaleX = value
    scaleY = value
}

fun Node.unscale() {
    scale(1.0)
}
