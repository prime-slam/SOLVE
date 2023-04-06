package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Size
import io.github.palexdev.mfxcore.collections.ObservableGrid
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import io.github.palexdev.virtualizedfx.utils.VSPUtils
import javafx.beans.property.DoubleProperty
import solve.scene.model.VisualizationFrame
import solve.scene.view.FrameView
import solve.scene.view.Grid
import solve.scene.view.GridProvider
import solve.scene.view.association.OutOfFramesLayer
import solve.utils.structures.Size as DoubleSize

object VirtualizedFXGridProvider : GridProvider {
    override fun createGrid(
        data: List<VisualizationFrame?>,
        columnsNumber: Int,
        cellSize: DoubleSize,
        scale: DoubleProperty,
        outOfFramesLayer: OutOfFramesLayer,
        cellFactory: (VisualizationFrame?) -> FrameView
    ): Grid {
        val gridData = ObservableGrid.fromList(data, columnsNumber)
        val grid = VirtualGrid(gridData) { item -> FrameViewAdapter(cellFactory(item)) }
        grid.cellSize = Size(cellSize.width * scale.value, cellSize.height * scale.value)
        grid.prefHeight = Int.MAX_VALUE.toDouble()

        val vsp = wrapGridWithVsp(grid, outOfFramesLayer)
        vsp.isAutoHideBars = true
        vsp.isSmoothScroll = true
        // Set up scrolling speed to achieve smooth scrolling
        VSPUtils.setVSpeed(vsp, 100.0, 100.0, 100.0)

        grid.setOnScroll { event ->
            event.consume() // Avoid vsp scrolling if mouse is on the grid pane
        }

        return VirtualizedFXGrid(grid, vsp, scale, cellSize)
    }
}
