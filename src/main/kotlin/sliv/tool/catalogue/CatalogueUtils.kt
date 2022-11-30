package sliv.tool.catalogue

import io.github.palexdev.mfxcore.utils.fx.SwingFXUtils.fromFXImage
import io.github.palexdev.mfxcore.utils.fx.SwingFXUtils.toFXImage
import javafx.geometry.Point2D
import javafx.scene.control.ListView
import javafx.scene.image.Image
import sliv.tool.project.model.ProjectFrame
import sliv.tool.project.model.ProjectLayer
import tornadofx.View
import tornadofx.onChange
import java.awt.image.BufferedImage
import kotlin.math.ceil

fun <T> ListView<T>.selectAllItems() = this.selectionModel.selectAll()

fun <T> ListView<T>.deselectAllItems() = this.selectionModel.clearSelection()

val <T> ListView<T>.selectedItems: List<T>
    get() = this.selectionModel.selectedItems

val <T> ListView<T>.selectedItemsCount: Int
    get() = this.selectedItems.count()

val <T> ListView<T>.selectedIndices: List<Int>
    get() = this.selectionModel.selectedIndices

fun <T> ListView<T>.selectItem(item: T) = this.selectionModel.select(item)

val ProjectFrame.layers: List<ProjectLayer>
    get() = this.landmarkFiles.map { it.projectLayer }.distinct()

inline fun <T> ListView<T>.onSelectionChanged(crossinline action: () -> Unit) {
    this.selectionModel.selectedItemProperty().onChange {
        action()
    }
}

fun View.containsPoint(x: Double, y: Double) = this.root.contains(Point2D(x, y))

fun Double.ceil(): Int = ceil(this).toInt()

fun combineImagesVertically(images: List<Image>): Image {
    val combinedImageWidth = images.maxOf { it.width }.ceil()
    val combinedImageHeight = images.sumOf { it.height }.ceil()
    val combinedImage = BufferedImage(combinedImageWidth, combinedImageHeight, BufferedImage.TYPE_INT_ARGB)

    val graphics = combinedImage.createGraphics()
    var drawYOffset = 0
    images.forEach {
        val bufferedImage = fromFXImage(it, null)
        graphics.drawImage(bufferedImage, 0, drawYOffset, null)
        drawYOffset += it.height.ceil()
    }
    graphics.dispose()

    return toFXImage(combinedImage, null)
}
