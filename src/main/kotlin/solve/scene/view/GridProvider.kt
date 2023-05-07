package solve.scene.view

import javafx.beans.property.DoubleProperty
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.OutOfFramesLayer
import solve.utils.structures.Size as DoubleSize

interface GridProvider {
    /**
     * Creates a new grid with specified data, and frames size.
     * Wraps grid with scroll pane containing out of frames layer in its visual tree.
     * Out of frames layer is responsible to draw association elements.
     */
    fun createGrid(
        data: List<VisualizationFrame?>,
        columnsNumber: Int,
        cellSize: DoubleSize,
        scale: DoubleProperty,
        outOfFramesLayer: OutOfFramesLayer,
        cellFactory: (VisualizationFrame?) -> FrameView
    ): Grid
}
