package solve.utils

import javafx.scene.control.ListView
import tornadofx.onChange

fun <T> ListView<T>.selectAllItems() = selectionModel.selectAll()

fun <T> ListView<T>.deselectAllItems() = selectionModel.clearSelection()

val <T> ListView<T>.selectedItems: List<T>
    get() = selectionModel.selectedItems

val <T> ListView<T>.selectedItemsCount: Int
    get() = selectedItems.count()

val <T> ListView<T>.selectedIndices: List<Int>
    get() = selectionModel.selectedIndices

fun <T> ListView<T>.selectItem(item: T) = selectionModel.select(item)

inline fun <T> ListView<T>.onSelectionChanged(crossinline action: () -> Unit) {
    selectionModel.selectedItemProperty().onChange {
        action()
    }
}


