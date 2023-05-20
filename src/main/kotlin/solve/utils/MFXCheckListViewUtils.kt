package solve.utils

import io.github.palexdev.materialfx.controls.MFXCheckListView

fun <T> MFXCheckListView<T>.checkAllItems() = selectionModel.selectItems(items)

fun <T> MFXCheckListView<T>.uncheckAllItems() = selectionModel.clearSelection()

val <T> MFXCheckListView<T>.checkedItems: List<T>
    get() = selectionModel.selectedValues

val <T> MFXCheckListView<T>.checkedItemsCount: Int
    get() = selectionModel.selectedValues.count()
