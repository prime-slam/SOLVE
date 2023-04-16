package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.collections.ObservableGrid

fun<T> List<T?>.toGridData(columnsNumber: Int): ObservableGrid<T?> {
    val emptyFrames = List((columnsNumber - count() % columnsNumber) % columnsNumber) { null }
    return ObservableGrid.fromList(this + emptyFrames, columnsNumber)
}