package solve.scene.view

import javafx.beans.property.DoubleProperty
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.OutOfFramesLayer
import solve.utils.structures.Size as DoubleSize

interface GridProvider {
    fun createGrid(
        data: List<VisualizationFrame?>,
        columnsNumber: Int,
        cellSize: DoubleSize,
        scale: DoubleProperty,
        outOfFramesLayer: OutOfFramesLayer,
        cellFactory: (VisualizationFrame?) -> FrameView
    ): Grid
}
