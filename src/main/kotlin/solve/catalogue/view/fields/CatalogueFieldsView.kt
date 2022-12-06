package solve.catalogue.view.fields

import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.Labeled
import javafx.scene.control.ListView
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
import tornadofx.*
import kotlin.math.min

abstract class CatalogueFieldsView: View(), SelectionNode {
    protected val fields: ObservableList<CatalogueField> by param()

    protected abstract val dragViewMaxFieldsNumber: Int
    protected abstract val listViewCellHeight: Double
    protected abstract val fieldsListView: ListView<CatalogueField>

    private val controller: CatalogueController by inject()
    private val sceneView: SceneView by inject()

    override val areSelectedAllItems: Boolean
        get() = fieldsListView.selectedItemsCount == controller.model.frames.count()
    override val isSelectionEmpty: Boolean
        get() = fieldsListView.selectedItems.isEmpty()
    override val selectedItems: List<CatalogueField>
        get() = fieldsListView.selectedItems
    override val selectedFrames: List<ProjectFrame>
        get() = fieldsListView.selectedItems.map { it.frame }

    private var isDragging = false

    protected open fun setFileNamesListViewCellFormat(labeled: Labeled, item: CatalogueField?) {
        labeled.prefHeight = listViewCellHeight
    }

    override fun selectAllItems() = fieldsListView.selectAllItems()

    override fun deselectAllItems() = fieldsListView.deselectAllItems()

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
        val dragboard = root.startDragAndDrop(TransferMode.MOVE)
        val clipboardContent = ClipboardContent().apply { putString("") }
        dragboard.setContent(clipboardContent)
        dragboard.dragView = createFileNameFieldsSnapshot(fieldsListView.selectedItems)
        isDragging = true
    }

    private fun createFileNameFieldsSnapshot(fields: List<CatalogueField>): Image {
        val snapshotFields = fields.take(dragViewMaxFieldsNumber).asObservable()
        val prefSnapshotHeight = (snapshotFields.count() * listViewCellHeight).floor()

        val fieldsSnapshotNode = listview(snapshotFields) {
            cellFormat {
                setFileNamesListViewCellFormat(this, it)
            }
        }
        fieldsListView.getChildList()?.remove(fieldsSnapshotNode)
        val snapshotScene = Scene(fieldsSnapshotNode)

        val nodeSnapshot = fieldsSnapshotNode.snapshot(null, null)
        return WritableImage(
            nodeSnapshot.pixelReader, nodeSnapshot.width.floor(), min(nodeSnapshot.height.floor(), prefSnapshotHeight)
        )
    }
}
