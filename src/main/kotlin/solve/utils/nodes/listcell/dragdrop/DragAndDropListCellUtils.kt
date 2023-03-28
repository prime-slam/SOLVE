package solve.utils.nodes.listcell.dragdrop

import javafx.scene.control.ListView

fun <T> isListViewCellSource(listView: ListView<T>, gestureSource: Any): Boolean =
    gestureSource is DragAndDropListCell<*> && listView.items.contains(gestureSource.item)
