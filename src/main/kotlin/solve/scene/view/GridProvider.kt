package solve.scene.view

import javafx.beans.property.DoubleProperty
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.OutOfFramesLayer

interface GridProvider {
    fun createGrid(
        data: List<VisualizationFrame?>,
        columnsNumber: Int,
        cellWidth: Double,
        cellHeight: Double,
        scale: DoubleProperty,
        outOfFramesLayer: OutOfFramesLayer,
        cellFactory: (VisualizationFrame?) -> FrameView
    ): Grid
}
