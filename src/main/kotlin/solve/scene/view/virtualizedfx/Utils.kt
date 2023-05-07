package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.collections.ObservableGrid

/**
 * Allows to show data with empty frames, if data size is not multiple of columns number.
 */
fun<T> List<T?>.toGridData(columnsNumber: Int): ObservableGrid<T?> {
    val emptyFrames = List((columnsNumber - count() % columnsNumber) % columnsNumber) { null }
    return ObservableGrid.fromList(this + emptyFrames, columnsNumber)
}
