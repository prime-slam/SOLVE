package solve.settings.visualization.fields.view

import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.util.Callback
import solve.scene.controller.SceneController
import solve.settings.visualization.fields.controller.VisualizationSettingsLayersController
import solve.utils.nodes.listcell.dragdrop.isListViewCellSource
import tornadofx.*

class VisualizationSettingsLayersView: View() {
    private val sceneController: SceneController by inject()
    private val controller: VisualizationSettingsLayersController by inject()

    private val fieldListViewCells = mutableListOf<VisualizationSettingsLayerCell>()

    private val fieldsListView = listview(controller.model.layers) {
        cellFactory = Callback {
            val layerCell = VisualizationSettingsLayerCell(sceneController)
            fieldListViewCells.add(layerCell)

            return@Callback layerCell
        }
        vgrow = Priority.ALWAYS
    }

    override val root = fieldsListView

    init {
        fieldsListView.setOnDragOver { event ->
            if (isListViewCellSource(fieldsListView, event.gestureSource)) {
                event.acceptTransferModes(TransferMode.MOVE)
            }
        }
        fieldsListView.setOnDragDropped { event ->
            sortFieldsListViewCellsInItemsOrder()
            if (isListViewCellSource(fieldsListView, event.gestureSource)) {
                fieldListViewCells.last { it.item != null }.onDragDropped(event)
            }

            event.isDropCompleted = true
            event.consume()
        }
    }

    private fun sortFieldsListViewCellsInItemsOrder() {
        fieldListViewCells.sortBy {
            var itemIndex = fieldsListView.items.indexOf(it.item)
            if (itemIndex == -1) {
                itemIndex = fieldListViewCells.count()
            }

            return@sortBy itemIndex
        }
    }
}
