package solve.catalogue

import javafx.scene.Node
import javafx.scene.control.Labeled
import javafx.scene.control.ListView
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import tornadofx.add
import tornadofx.getChildList
import tornadofx.onChange
import tornadofx.tooltip

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

fun <T> synchronizeListViewsSelections(firstListView: ListView<T>, secondListView: ListView<T>) {
    secondListView.selectionModel = firstListView.selectionModel
}

fun Labeled.addNameTooltip() {
    tooltip(text)
}

fun Node.removeSafely(node: Node?) {
    node.let { getChildList()?.remove(it) }
}

fun Node.addSafely(node: Node?) {
    node?.let { add(it) }
}

