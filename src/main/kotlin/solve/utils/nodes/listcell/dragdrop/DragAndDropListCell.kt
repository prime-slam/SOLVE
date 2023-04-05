package solve.utils.nodes.listcell.dragdrop

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class DragAndDropListCell<T: Any>(private val itemType: KClass<T>) : ListCell<T>() {
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
            if (item == null) {
                return@setOnDragOver
            }

            if (isListViewCellSource(listView, event.gestureSource)) {
                event.acceptTransferModes(TransferMode.MOVE)
            }

            event.consume()
        }

        setOnDragEntered { event ->
            if (item == null) {
                return@setOnDragEntered
            }

            if (isListViewCellSource(listView, event.gestureSource)) {
                val itemInfo = createItemInfo(item)
                setOnDragEntered(event, itemInfo)
            }

            event.consume()
        }

        setOnDragExited { event ->
            if (item == null) {
                return@setOnDragExited
            }

            if (isListViewCellSource(listView, event.gestureSource)) {
                val itemInfo = createItemInfo(item)
                setOnDragExited(event, itemInfo)
            }

            event.consume()
        }

        setOnDragDropped { event ->
            if (item == null) {
                return@setOnDragDropped
            }

            onDragDropped(event)

            event.isDropCompleted = true
            event.consume()
        }
    }

    fun onDragDropped(event: DragEvent) {
        if (isListViewCellSource(listView, event.gestureSource)) {
            val droppedCellItem = itemType.cast(
                (event.gestureSource as? DragAndDropListCell<*>)?.item ?: return
            )
            if (!isAbleToDropItem(item, droppedCellItem)) {
                return
            }
            val droppedCellIndex = listView.items.indexOf(droppedCellItem)
            val thisItem = item

            listView.items[droppedCellIndex] = item
            listView.items[index] = droppedCellItem

            val thisItemInfo = createItemInfo(thisItem)
            val droppedItemInfo = createItemInfo(droppedCellItem)

            setAfterDragDropped(event, thisItemInfo, droppedItemInfo)
        }
    }

    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)

        if (!empty) {
            val itemGraphic = createItemCellGraphic(item)
            if (itemGraphic != null) {
                graphic = itemGraphic
            }
        } else {
            graphic = null
        }
    }

    protected open fun createItemCellGraphic(item: T): Node? = null

    protected open fun createItemDragView(item: T): Image? = null

    protected open fun createItemDragAndDropContent(item: T): Pair<DataFormat, Any>? = null

    protected open fun setOnDragEntered(event: DragEvent, itemInfo: DragAndDropCellItemInfo<T>) { }

    protected open fun setOnDragExited(event: DragEvent, itemInfo: DragAndDropCellItemInfo<T>) { }

    protected open fun setAfterDragDropped(
        event: DragEvent,
        thisItemInfo: DragAndDropCellItemInfo<T>,
        droppedItemInfo: DragAndDropCellItemInfo<T>
    ) { }

    protected open fun isAbleToDropItem(thisItem: T, droppedItem: T): Boolean = true

    private fun createItemInfo(item: T) = DragAndDropCellItemInfo(item, getItemIndex(item))

    private fun getItemIndex(item: T) = listView.items.indexOf(item)
}

data class DragAndDropCellItemInfo<T>(val item: T, val index: Int)
