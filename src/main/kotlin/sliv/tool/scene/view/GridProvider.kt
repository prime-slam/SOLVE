package sliv.tool.scene.view

import sliv.tool.scene.model.VisualizationFrame

interface GridProvider {
    fun createGrid(
        data: List<VisualizationFrame>,
        columnsNumber: Int,
        cellWidth: Double,
        cellHeight: Double,
        cellFactory: (VisualizationFrame) -> FrameView
    ): Grid
}