package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Size
import io.github.palexdev.mfxcore.collections.ObservableGrid
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import io.github.palexdev.virtualizedfx.utils.VSPUtils
import javafx.beans.property.DoubleProperty
import javafx.geometry.Orientation
import solve.scene.model.VisualizationFrame
import solve.scene.view.*
import tornadofx.onChange

object VirtualizedFXGridProvider : GridProvider {
    override fun createGrid(
        data: List<VisualizationFrame?>,
        columnsNumber: Int,
        cellWidth: Double,
        cellHeight: Double,
        scale: DoubleProperty,
        cellFactory: (VisualizationFrame?) -> FrameView
    ): Grid {
        val gridData = ObservableGrid.fromList(data, columnsNumber)
        val grid = VirtualGrid(gridData) { item -> FrameViewAdapter(cellFactory(item)) }
        grid.cellSize = Size(cellWidth, cellHeight)
        grid.prefHeight = Int.MAX_VALUE.toDouble()

        val vsp = VSPUtils.wrap(grid)
        vsp.isAutoHideBars = true
        vsp.isSmoothScroll = true
        // Set up scrolling speed to achieve smooth scrolling
        VSPUtils.setVSpeed(vsp, 100.0, 100.0, 100.0)

        scale.onChange { newScale ->
            // Virtual grid breaks if cell size changes when x position differs from 0.0
            // Virtual grid always drops position to (0, 0) when cell size changes, so this solution is ok
            grid.scrollTo(0.0, Orientation.HORIZONTAL)
            grid.scrollTo(0.0, Orientation.VERTICAL)
            grid.cellSize = Size(
                cellWidth * newScale, cellHeight * newScale
            )
        }

        grid.setOnScroll { event ->
            event.consume() // Avoid vsp scrolling if mouse is on the grid pane
        }

        return VirtualizedFXGrid(grid, vsp)
    }
}