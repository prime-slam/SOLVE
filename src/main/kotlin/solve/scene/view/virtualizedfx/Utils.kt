package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.collections.ObservableGrid

fun<T> List<T?>.toGridData(columnsNumber: Int): ObservableGrid<T?> {
    val emptyFrames = (0 until (columnsNumber - count() % columnsNumber) % columnsNumber).map { null }
    return ObservableGrid.fromList(this + emptyFrames, columnsNumber)
}