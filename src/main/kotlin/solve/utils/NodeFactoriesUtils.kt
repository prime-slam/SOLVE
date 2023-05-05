package solve.utils

import javafx.event.EventTarget
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.*

fun EventTarget.imageViewIcon(iconImage: Image, width: Double, op: ImageView.() -> Unit = {}): ImageView {
    val imageView = ImageView(iconImage)

    imageView.fitWidth = width
    imageView.isPreserveRatio = true
    imageView.attachTo(this, op)

    return imageView
}
