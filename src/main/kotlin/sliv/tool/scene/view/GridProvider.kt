package sliv.tool.scene.view

import javafx.scene.Node
import sliv.tool.scene.model.VisualizationFrame
import java.util.function.Function

interface GridProvider {
    fun createGrid(
        data: List<VisualizationFrame>,
        columnsNumber: Int,
        cellWidth: Double,
        cellHeight: Double,
        cellFactory: Function<VisualizationFrame, FrameView>
    ): Node
}