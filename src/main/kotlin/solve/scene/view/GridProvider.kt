package solve.scene.view

import javafx.beans.property.DoubleProperty
import solve.scene.model.VisualizationFrame

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