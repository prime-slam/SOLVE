package solve.utils

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

fun Node.addAll(nodes: Collection<Node>) {
    nodes.forEach {
        add(it)
    }
}

fun Node.clearChildren() {
    getChildList()?.clear()
}

fun createImageViewIcon(iconImage: Image, width: Double): ImageView {
    val imageView = ImageView(iconImage)
    imageView.fitWidth = width
    imageView.isPreserveRatio = true

    return imageView
}

fun createHGrowHBox() = HBox().also { it.hgrow = Priority.ALWAYS }

fun createInsetsWithValue(value: Double) = Insets(value, value, value, value)
