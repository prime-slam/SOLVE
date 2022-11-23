package sliv.tool.catalogue

import javafx.scene.control.ListView
import sliv.tool.project.model.ProjectFrame
import sliv.tool.project.model.ProjectLayer

fun <T> ListView<T>.selectAllItems() = this.selectionModel.selectAll()

fun <T> ListView<T>.deselectAllItems() = this.selectionModel.clearSelection()

fun <T> ListView<T>.selectedItems(): List<T> = this.selectionModel.selectedItems

fun <T> ListView<T>.selectedItemsCount(): Int = this.selectedItems().count()

fun <T> ListView<T>.selectItem(item: T) = this.selectionModel.select(item)

fun ProjectFrame.extractLayers(): List<ProjectLayer> {
    val layersSet = mutableSetOf<ProjectLayer>()
    this.landmarkFiles.forEach {layersSet.add(it.projectLayer) }

    return layersSet.toList()
}
