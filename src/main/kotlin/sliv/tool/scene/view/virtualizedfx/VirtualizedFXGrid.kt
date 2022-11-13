package sliv.tool.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Position
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.input.ScrollEvent
import sliv.tool.scene.model.VisualizationFrame
import sliv.tool.scene.view.Grid

class VirtualizedFXGrid(
    private val virtualGrid: VirtualGrid<VisualizationFrame, FrameViewAdapter>, private val vsp: VirtualScrollPane
) : Grid {
    private var dragStart = Position.of(-1.0, -1.0)
    private var initialValues = Position.of(0.0, 0.0)

    override fun setUpPanning() {
        virtualGrid.setOnMousePressed { event ->
            dragStart = Position.of(event.x, event.y)
            initialValues = virtualGrid.position
        }

        virtualGrid.setOnMouseDragged { event ->
            val xDelta = dragStart.x - event.x
            val yDelta = dragStart.y - event.y
            virtualGrid.scrollTo(initialValues.x + xDelta, Orientation.HORIZONTAL)
            virtualGrid.scrollTo(initialValues.y + yDelta, Orientation.VERTICAL)
        }
    }

    override fun setOnScroll(handler: EventHandler<ScrollEvent>) {
        virtualGrid.addEventHandler(ScrollEvent.SCROLL, handler)
    }

    override fun scrollTo(x: Double, y: Double) {
        virtualGrid.scrollTo(x, Orientation.HORIZONTAL)
        virtualGrid.scrollTo(y, Orientation.VERTICAL)
    }

    override fun getPosition(): Pair<Double, Double> {
        return virtualGrid.position.x to virtualGrid.position.y
    }

    override val node: Node = vsp
}