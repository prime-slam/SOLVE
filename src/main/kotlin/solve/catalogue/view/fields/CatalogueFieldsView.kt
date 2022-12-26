package solve.catalogue.view.fields

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Labeled
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import solve.catalogue.*
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.CatalogueField
import solve.project.model.ProjectFrame
import solve.scene.view.SceneView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.math.min

abstract class CatalogueFieldsView: View() {
    private val fields: ObservableList<CatalogueField> by param()
    val fieldsListView: ListView<CatalogueField> = listview(fields) {
        addStylesheet(CatalogueFieldsStyle::class)
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        cellFormat {
            setListViewCellFormat(this, it)
        }
        vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS
    }

    protected abstract val dragViewMaxFieldsNumber: Int
    protected abstract val listViewCellHeight: Double

    private val controller: CatalogueController by inject()
    private val sceneView: SceneView by inject()

    val areSelectedAllItems: Boolean
        get() = fieldsListView.selectedItemsCount == controller.model.frames.count()
    val isSelectionEmpty: Boolean
        get() = fieldsListView.selectedItems.isEmpty()
    val selectedItems: List<CatalogueField>
        get() = fieldsListView.selectedItems
    val selectedFrames: List<ProjectFrame>
        get() = fieldsListView.selectedItems.map { it.frame }

    private var isDragging = false

    fun selectAllItems() = fieldsListView.selectAllItems()

    fun deselectAllItems() = fieldsListView.deselectAllItems()

    protected open fun setListViewCellFormat(labeled: Labeled, item: CatalogueField?) {
        labeled.prefHeight = listViewCellHeight
    }

    protected abstract fun createFieldsSnapshotNode(fields: List<CatalogueField>): Node

    protected fun initialize() {
        initializeDragEvents()
        initializeInteractionEvent()
    }

    private fun initializeInteractionEvent() {
        fieldsListView.setOnMouseClicked {
            controller.onFieldsSelectionChanged()
        }
        fieldsListView.onSelectionChanged {
            controller.onFieldsSelectionChanged()
        }
    }

    private fun initializeDragEvents() {
        fieldsListView.setOnDragDetected {
            onCatalogueDragDetected()
        }
        sceneView.root.addEventFilter(DragEvent.DRAG_OVER, ::onSceneDragOver)
        sceneView.root.addEventFilter(DragEvent.DRAG_DROPPED, ::onSceneDragDropped)
    }

    private fun onSceneDragDropped(event: DragEvent) {
        if (isDragging) {
            controller.visualizeFramesSelection(selectedFrames)
        }

        isDragging = false
    }

    private fun onSceneDragOver(event: DragEvent) {
        if (isDragging) {
            event.acceptTransferModes(TransferMode.MOVE)
        }
    }

    private fun onCatalogueDragDetected() {
        val selectedFields = fieldsListView.selectedItems
        if (selectedFields.isEmpty()) {
            return
        }

        val dragboard = root.startDragAndDrop(TransferMode.MOVE)
        val clipboardContent = ClipboardContent().apply { putString("") }
        dragboard.setContent(clipboardContent)
        dragboard.dragView = createFileNameFieldsSnapshot(fieldsListView.selectedItems)
        isDragging = true
    }

    private fun createFileNameFieldsSnapshot(fields: List<CatalogueField>): Image {
        val snapshotFields = fields.take(dragViewMaxFieldsNumber)
        val prefSnapshotHeight = (snapshotFields.count() * listViewCellHeight).floor()

        val fieldsSnapshotNode = createFieldsSnapshotNode(snapshotFields)
        fieldsListView.getChildList()?.remove(fieldsSnapshotNode)
        val snapshotScene = Scene(fieldsSnapshotNode as Parent)

        val nodeSnapshot = fieldsSnapshotNode.snapshot(null, null)
        return WritableImage(
            nodeSnapshot.pixelReader, nodeSnapshot.width.floor(), min(nodeSnapshot.height.floor(), prefSnapshotHeight)
        )
    }
}

class CatalogueFieldsStyle : Stylesheet() {
    companion object {
        private val SelectedFieldColor = Color.valueOf("#0096c9")
    }

    init {
        listView {
            cell {
                and(selected) {
                    backgroundColor += SelectedFieldColor
                }
            }
        }
    }
}