package solve.utils

import javafx.scene.image.Image
import javafx.scene.image.ImageView

fun createImageViewIcon(iconImage: Image, width: Double): ImageView {
    val imageView = ImageView(iconImage)
    imageView.fitWidth = width
    imageView.isPreserveRatio = true

    return imageView
}
