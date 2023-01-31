package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Position
import io.github.palexdev.mfxcore.base.bindings.MFXBindings
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.input.ScrollEvent
import solve.scene.model.VisualizationFrame
import solve.scene.view.Grid

class VirtualizedFXGrid(
    private val virtualGrid: VirtualGrid<VisualizationFrame?, FrameViewAdapter>, private val vsp: VirtualScrollPane
) : Grid {
    private var dragStartMousePosition = Position.of(-1.0, -1.0)
    private var dragStartGridPosition = Position.of(0.0, 0.0)

    override val currentPosition: Pair<Double, Double>
        get() = virtualGrid.position.x to virtualGrid.position.y

    override val node: Node = vsp

    override fun setUpPanning() {
        virtualGrid.setOnMousePressed { event ->
            dragStartMousePosition = Position.of(event.x, event.y)
            dragStartGridPosition = virtualGrid.position
        }

        virtualGrid.setOnMouseDragged { event ->
            val xDelta = dragStartMousePosition.x - event.x
            val yDelta = dragStartMousePosition.y - event.y
            virtualGrid.scrollTo(dragStartGridPosition.x + xDelta, Orientation.HORIZONTAL)
            virtualGrid.scrollTo(dragStartGridPosition.y + yDelta, Orientation.VERTICAL)
        }
    }

    override fun setOnScroll(handler: EventHandler<ScrollEvent>) {
        virtualGrid.addEventHandler(ScrollEvent.SCROLL, handler)
    }

    override fun scrollTo(x: Double, y: Double) {
        virtualGrid.scrollTo(x, Orientation.HORIZONTAL)
        virtualGrid.scrollTo(y, Orientation.VERTICAL)
    }

    override fun dispose() {
        val bindings = MFXBindings.instance()
        bindings.unbindBidirectional(vsp.vValProperty())
        bindings.unbindBidirectional(vsp.hValProperty())
    }
}