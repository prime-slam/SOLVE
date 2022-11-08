package sliv.tool.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Size
import io.github.palexdev.mfxcore.collections.ObservableGrid
import io.github.palexdev.virtualizedfx.enums.ScrollPaneEnums
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import io.github.palexdev.virtualizedfx.utils.VSPUtils
import javafx.beans.property.DoubleProperty
import sliv.tool.scene.model.VisualizationFrame
import sliv.tool.scene.view.*
import tornadofx.onChange

object VirtualizedFXGridProvider : GridProvider {
    override fun createGrid(
        data: List<VisualizationFrame>,
        columnsNumber: Int,
        cellWidth: Double,
        cellHeight: Double,
        scale: DoubleProperty,
        cellFactory: (VisualizationFrame) -> FrameView
    ): Grid {
        val gridData = ObservableGrid.fromList(data, columnsNumber)
        val grid = VirtualGrid(gridData) { item -> FrameViewAdapter(cellFactory(item)) }
        grid.cellSize = Size(cellWidth, cellHeight)

        val vsp = VSPUtils.wrap(grid)
        vsp.layoutMode = ScrollPaneEnums.LayoutMode.COMPACT
        vsp.isAutoHideBars = true
        vsp.isSmoothScroll = true
        VSPUtils.setVSpeed(vsp, 50.0, 0.0, 50.0) //TODO: temporary solution to avoid vsp scrolling

        scale.onChange { newScale ->
            try {
                grid.cellSize = Size(
                    cellWidth * newScale, cellHeight * newScale
                ) //TODO: in many cases VirtualGrid drops position when cell size changed
            } catch (e: Exception) {
                println("Cell size exception") //TODO: bug in VirtualGrid
            }
        }

        return VirtualizedFXGrid(grid, vsp)
    }
}