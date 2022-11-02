package sliv.tool.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Size
import io.github.palexdev.mfxcore.collections.ObservableGrid
import io.github.palexdev.virtualizedfx.enums.ScrollPaneEnums
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import io.github.palexdev.virtualizedfx.utils.VSPUtils
import javafx.scene.Node
import java.util.function.Function
import sliv.tool.scene.model.VisualizationFrame
import sliv.tool.scene.view.*

object VirtualizedFXGridProvider : GridProvider {
    override fun createGrid(
        data: List<VisualizationFrame>,
        columnsNumber: Int,
        cellWidth: Double,
        cellHeight: Double,
        cellFactory: Function<VisualizationFrame, FrameView>
    ): Node {
        val gridData = ObservableGrid.fromList(data, columnsNumber)
        val grid = VirtualGrid(gridData) { item -> FrameViewAdapter(cellFactory.apply(item)) }
        grid.cellSize = Size(cellWidth, cellHeight)

        val vsp = VSPUtils.wrap(grid)
        vsp.layoutMode = ScrollPaneEnums.LayoutMode.COMPACT
        vsp.isAutoHideBars = true

        return vsp
    }
}