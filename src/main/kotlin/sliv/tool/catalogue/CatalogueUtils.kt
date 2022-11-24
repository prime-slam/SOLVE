package sliv.tool.catalogue

import javafx.scene.control.ListView
import javafx.scene.control.Toggle
import javafx.scene.control.ToggleGroup
import sliv.tool.project.model.ProjectFrame
import sliv.tool.project.model.ProjectLayer
import tornadofx.onChange

fun <T> ListView<T>.selectAllItems() = this.selectionModel.selectAll()

fun <T> ListView<T>.deselectAllItems() = this.selectionModel.clearSelection()

val <T> ListView<T>.selectedItems: List<T>
    get() = this.selectionModel.selectedItems

val <T> ListView<T>.selectedItemsCount: Int
    get() = this.selectedItems.count()

fun <T> ListView<T>.selectItem(item: T) = this.selectionModel.select(item)

val ProjectFrame.layers: List<ProjectLayer>
    get() = this.landmarkFiles.map { it.projectLayer }.distinct()

inline fun <T> ListView<T>.onSelectionChanged(crossinline action: () -> Unit) {
    this.selectionModel.selectedItemProperty().onChange {
        action()
    }
}
