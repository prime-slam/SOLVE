package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Position
import io.github.palexdev.mfxcore.base.bindings.MFXBindings
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.input.ScrollEvent
import solve.scene.model.VisualizationFrame
import solve.scene.view.Grid
import tornadofx.onChange

class VirtualizedFXGrid(
    private val virtualGrid: VirtualGrid<VisualizationFrame?, FrameViewAdapter>, private val vsp: VirtualScrollPane
) : Grid {
    private var dragStartMousePosition = Position.of(-1.0, -1.0)
    private var dragStartGridPosition = Position.of(0.0, 0.0)

    override val xProperty = SimpleDoubleProperty(virtualGrid.position.x)
    override val yProperty = SimpleDoubleProperty(virtualGrid.position.y)

    override val node: Node = vsp

    init {
        virtualGrid.positionProperty().onChange { position ->
            xProperty.value = position?.x ?: 0.0
            yProperty.value = position?.y ?: 0.0
        }

        xProperty.onChange { newX ->
            virtualGrid.scrollTo(newX, Orientation.HORIZONTAL)
        }

        yProperty.onChange { newY ->
            virtualGrid.scrollTo(newY, Orientation.VERTICAL)
        }
    }

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

    override fun dispose() {
        val bindings = MFXBindings.instance()
        bindings.unbindBidirectional(vsp.vValProperty())
        bindings.unbindBidirectional(vsp.hValProperty())
    }
}