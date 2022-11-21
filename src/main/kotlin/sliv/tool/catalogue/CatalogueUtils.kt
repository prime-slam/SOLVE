package sliv.tool.catalogue

import javafx.scene.control.ListView
import sliv.tool.project.model.ProjectFrame

fun <T> ListView<T>.selectAllItems() = this.selectionModel.selectAll()

fun <T> ListView<T>.deselectAllItems() = this.selectionModel.clearSelection()

fun <T> ListView<T>.selectedItems(): List<T> = this.selectionModel.selectedItems
