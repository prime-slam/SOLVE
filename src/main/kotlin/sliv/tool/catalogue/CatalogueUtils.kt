package sliv.tool.catalogue

import javafx.scene.control.ListView
import javafx.scene.image.Image
import sliv.tool.project.model.ProjectFrame
import sliv.tool.project.model.ProjectLayer
import tornadofx.onChange
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

fun Double.ceil(): Int = ceil(this).toInt()

fun Double.floor(): Int = kotlin.math.floor(this).toInt()

fun loadImage(name: String): Image? {
    val imageFile = Any::class::class.java.getResource(name)?.openStream() ?: return null

    return Image(imageFile)
}