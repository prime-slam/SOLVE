package solve.catalogue

import javafx.collections.ListChangeListener
import javafx.scene.control.ListView
import javafx.scene.image.Image
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import tornadofx.onChange
import tornadofx.selectAll
import kotlin.math.ceil

fun <T> ListView<T>.selectAllItems() = selectionModel.selectAll()

fun <T> ListView<T>.deselectAllItems() = selectionModel.clearSelection()

val <T> ListView<T>.selectedItems: List<T>
    get() = selectionModel.selectedItems

val <T> ListView<T>.selectedItemsCount: Int
    get() = selectedItems.count()

val <T> ListView<T>.selectedIndices: List<Int>
    get() = selectionModel.selectedIndices

fun <T> ListView<T>.selectItem(item: T) = selectionModel.select(item)

val ProjectFrame.layers: List<ProjectLayer>
    get() = landmarkFiles.map { it.projectLayer }.distinct()

inline fun <T> ListView<T>.onSelectionChanged(crossinline action: () -> Unit) {
    selectionModel.selectedItemProperty().onChange {
        action()
    }
}

fun Double.ceil(): Int = ceil(this).toInt()

fun Double.floor(): Int = kotlin.math.floor(this).toInt()

fun loadImage(name: String): Image? {
    val imageFile = Any::class::class.java.getResource(name)?.openStream() ?: return null

    return Image(imageFile)
}

fun <T> synchronizeListViewsSelections(firstListView: ListView<T>, secondListView: ListView<T>) {
    secondListView.selectionModel = firstListView.selectionModel
}
