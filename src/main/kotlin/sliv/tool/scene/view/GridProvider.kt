package sliv.tool.scene.view

import javafx.beans.property.DoubleProperty
import sliv.tool.scene.model.VisualizationFrame

interface GridProvider {
    fun createGrid(
        data: List<VisualizationFrame>,
        columnsNumber: Int,
        cellWidth: Double,
        cellHeight: Double,
        scale: DoubleProperty,
        cellFactory: (VisualizationFrame) -> FrameView
    ): Grid
}