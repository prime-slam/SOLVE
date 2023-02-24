package solve.utils.nodes

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode

abstract class DragAndDropListCell<T> : ListCell<T>() {
    init {
        contentDisplay = ContentDisplay.CENTER
        alignment = Pos.CENTER

        setOnDragDetected { event ->
            if (item == null) {
                return@setOnDragDetected
            }

            val dragboard = startDragAndDrop(TransferMode.MOVE)
            val clipboardContent = ClipboardContent()
            val contentData = createItemDragAndDropContent(item)
            if (contentData != null) {
                clipboardContent[contentData.first] = contentData.second
            } else {
                clipboardContent.putString("")
            }

            dragboard.dragView = createItemDragView(item)
            dragboard.setContent(clipboardContent)

            event.consume()
        }

        setOnDragOver { event ->
            if (isListViewCellSource(event.gestureSource)) {
                event.acceptTransferModes(TransferMode.MOVE)
            }

            event.consume()
        }

        setOnDragEntered { event ->
            if (isListViewCellSource(event.gestureSource)) {
                setOnDragEntered(event, item)
            }

            event.consume()
        }

        setOnDragExited { event ->
            if (isListViewCellSource(event.gestureSource)) {
                setOnDragExited(event, item)
            }

            event.consume()
        }

        setOnDragDropped { event ->
            if (item == null) {
                return@setOnDragDropped
            }
            if (isListViewCellSource(event.gestureSource)) {
                val droppedCellItem =
                    (event.gestureSource as DragAndDropListCell<*>).item as? T ?: return@setOnDragDropped
                val droppedCellIndex = listView.items.indexOf(droppedCellItem)

                listView.items[droppedCellIndex] = item
                listView.items[index] = droppedCellItem
            }

            event.isDropCompleted = true
            event.consume()
        }

        setOnDragDone(DragEvent::consume)
    }

    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)

        if (item != null && !empty) {
            val itemGraphic = createItemCellGraphic(item)
            if (itemGraphic != null) {
                graphic = itemGraphic
            }
        } else {
            graphic = null
        }
    }


    private fun isListViewCellSource(gestureSource: Any): Boolean =
        gestureSource is DragAndDropListCell<*> && listView.items.contains(gestureSource.item)

    protected open fun createItemCellGraphic(item: T): Node? = null

    protected open fun createItemDragView(item: T): Image? = null

    protected open fun createItemDragAndDropContent(item: T): Pair<DataFormat, Any>? = null

    protected open fun setOnDragEntered(event: DragEvent, item: T) { }

    protected open fun setOnDragExited(event: DragEvent, item: T) { }

    protected open fun setOnDragDropped(event: DragEvent) { }
}
