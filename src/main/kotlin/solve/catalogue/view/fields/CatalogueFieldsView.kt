package solve.catalogue.view.fields

import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell
import io.github.palexdev.materialfx.effects.DepthLevel
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.CatalogueField
import solve.project.model.ProjectFrame
import solve.scene.view.SceneView
import solve.utils.floorToInt
import solve.styles.CatalogueViewStylesheet
import solve.utils.*
import tornadofx.*
import kotlin.math.min

abstract class CatalogueFieldsView : View() {
    private val fields: ObservableList<CatalogueField> by param()

    val fieldsListView = mfxCheckListView(fields) {
        padding = Insets(0.0, 0.0, 0.0, 0.0)

        addStylesheet(CatalogueViewStylesheet::class)

        depthLevel = DepthLevel.LEVEL0

        setCellFactory {
            val cell =
                object : MFXCheckListCell<CatalogueField>(this, it) {
                    override fun updateItem(item: CatalogueField?) {
                        super.updateItem(item)
                        setListViewCellFormat(label, item)
                    }
                }

            setCellHeight(cell)
            return@setCellFactory cell
        }
        prefHeight = 3000.0
        prefWidth = 500.0

        vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS
    }

    protected abstract val dragViewMaxFieldsNumber: Int
    protected abstract val listViewCellHeight: Double

    private val controller: CatalogueController by inject()
    private val sceneView: SceneView by inject()

    val areCheckedAllItems: Boolean
        get() = fieldsListView.checkedItemsCount == controller.model.frames.count()
    val isSelectionEmpty: Boolean
        get() = fieldsListView.checkedItems.isEmpty()
    val selectedItems: List<CatalogueField>
        get() = fieldsListView.checkedItems
    val checkedFrames: List<ProjectFrame>
        get() = fieldsListView.checkedItems.map { it.frame }

    private var isDragging = false

    fun checkAllItems() = fieldsListView.checkAllItems()

    fun uncheckAllItems() = fieldsListView.uncheckAllItems()

    protected fun setCellHeight(cell: MFXCheckListCell<CatalogueField>) {
        cell.prefHeight = listViewCellHeight
    }

    protected open fun setListViewCellFormat(
        label: Label,
        item: CatalogueField?
    ) {
        label.textProperty().unbind()
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
            controller.visualizeFramesSelection(checkedFrames)
        }

        isDragging = false
    }

    private fun onSceneDragOver(event: DragEvent) {
        if (isDragging) {
            event.acceptTransferModes(TransferMode.MOVE)
        }
    }

    private fun onCatalogueDragDetected() {
        val selectedFields = fieldsListView.checkedItems
        if (selectedFields.isEmpty()) {
            return
        }

        val dragboard = root.startDragAndDrop(TransferMode.MOVE)
        val clipboardContent = ClipboardContent().apply { putString("") }
        dragboard.setContent(clipboardContent)
        dragboard.dragView = createFileNameFieldsSnapshot(fieldsListView.checkedItems)
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
