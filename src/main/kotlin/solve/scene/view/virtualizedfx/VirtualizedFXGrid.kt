package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Position
import io.github.palexdev.mfxcore.base.beans.Size
import io.github.palexdev.mfxcore.base.bindings.MFXBindings
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import javafx.beans.InvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.input.ScrollEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import solve.scene.model.VisualizationFrame
import solve.scene.view.Grid
import solve.utils.structures.Size as DoubleSize
import tornadofx.onChange

class VirtualizedFXGrid(
    private val virtualGrid: VirtualGrid<VisualizationFrame?, FrameViewAdapter>,
    private val vsp: VirtualScrollPane,
    private val scaleProperty: DoubleProperty,
    private val cellSize: DoubleSize
) : Grid {
    private var dragStartMousePosition = Position.of(-1.0, -1.0)
    private var dragStartGridPosition = Position.of(0.0, 0.0)

    override val node: Node = vsp

    override val xProperty = SimpleDoubleProperty(virtualGrid.position.x)
    override val yProperty = SimpleDoubleProperty(virtualGrid.position.y)

    private val scaleChangedListener = InvalidationListener {
        // Virtual grid can't lay out columns correctly if position differs from (0, 0)
        scrollX(0.0)
        scrollY(0.0)
        val newScale = scaleProperty.value
        virtualGrid.cellSize = Size(cellSize.width * newScale, cellSize.height * newScale)
    }

    init {
        scaleProperty.addListener(scaleChangedListener)
        virtualGrid.positionProperty().onChange { position ->
            xProperty.set(position?.x ?: 0.0)
            yProperty.set(position?.y ?: 0.0)
        }
    }

    fun changeColumnsNumber(newColumnsNumber: Int) {
        if (newColumnsNumber < 0) {
            println("Columns number of the scene grid should be a positive number!")
            return
        }

        val gridItems = virtualGrid.items.toList()
        virtualGrid.clear()

        val fullRowsNumber = gridItems.lastIndex / newColumnsNumber
        for (i in 0 until fullRowsNumber) {
            val addingRowItems = gridItems.subList(i * newColumnsNumber, (i + 1) * newColumnsNumber)
            virtualGrid.items.addRow(addingRowItems)
        }

        val firstRemainingItemIndex = fullRowsNumber * newColumnsNumber
        val lastRowItems = gridItems.slice(firstRemainingItemIndex..gridItems.lastIndex).toMutableList()
        repeat(newColumnsNumber - lastRowItems.count()) {
            lastRowItems.add(null)
        }
        virtualGrid.items.addRow(lastRowItems)
    }

    override fun scrollX(newX: Double): Double {
        virtualGrid.scrollTo(newX, Orientation.HORIZONTAL)
        return virtualGrid.position.x
    }

    override fun scrollY(newY: Double): Double {
        virtualGrid.scrollTo(newY, Orientation.VERTICAL)
        return virtualGrid.position.y
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

    override fun setOnMouseWheel(handler: EventHandler<ScrollEvent>) {
        virtualGrid.addEventHandler(ScrollEvent.SCROLL, handler)
    }

    override fun dispose() {
        val bindings = MFXBindings.instance()
        bindings.unbindBidirectional(vsp.vValProperty())
        bindings.unbindBidirectional(vsp.hValProperty())
        scaleProperty.removeListener(scaleChangedListener)
        virtualGrid.indexedCells.values.forEach { frame ->
            frame.dispose()
        }
    }
}
