package solve.catalogue.view.fields

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Labeled
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
<<<<<<< HEAD
=======
import solve.catalogue.*
>>>>>>> 13f8702 (Just an automaticly formatted codestyle)
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.CatalogueField
import solve.project.model.ProjectFrame
import solve.scene.view.SceneView
<<<<<<< HEAD
import solve.utils.deselectAllItems
import solve.utils.floorToInt
import solve.utils.onSelectionChanged
import solve.utils.selectAllItems
import solve.utils.selectedItems
import solve.utils.selectedItemsCount
=======
import solve.utils.*
>>>>>>> 13f8702 (Just an automaticly formatted codestyle)
import tornadofx.*
import kotlin.math.min

abstract class CatalogueFieldsView : View() {
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

    private fun onSceneDragDropped(@Suppress("UNUSED_PARAMETER") event: DragEvent) {
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
        val prefSnapshotHeight = (snapshotFields.count() * listViewCellHeight).floorToInt()

        val fieldsSnapshotNode = createFieldsSnapshotNode(snapshotFields)
        fieldsListView.getChildList()?.remove(fieldsSnapshotNode)

        val nodeSnapshot = fieldsSnapshotNode.snapshot(null, null)
        return WritableImage(
            nodeSnapshot.pixelReader,
            nodeSnapshot.width.floorToInt(),
            min(nodeSnapshot.height.floorToInt(), prefSnapshotHeight)
        )
    }
}

class CatalogueFieldsStyle : Stylesheet() {
    init {
        listView {
            cell {
                and(selected) {
                    backgroundColor += SelectedFieldColor
                }
            }
        }
    }

    companion object {
        private val SelectedFieldColor = Color.valueOf("#0096c9")
    }
}
