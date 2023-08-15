package solve.interactive.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.collections.ObservableGrid
import io.github.palexdev.virtualizedfx.cell.GridCell
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import io.github.palexdev.virtualizedfx.utils.VSPUtils
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseButton
import javafx.scene.input.ScrollEvent
import javafx.scene.shape.Rectangle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import solve.fireMouseDragged
import solve.fireMousePressed
import solve.fireScrollEvent
import solve.interactive.InteractiveTestClass
import solve.scene.model.Landmark
import solve.scene.model.VisualizationFrame
import solve.scene.view.FrameView
import solve.scene.view.FrameViewParameters
import solve.scene.view.association.AssociationsManager
import solve.scene.view.association.OutOfFramesLayer
import solve.scene.view.virtualizedfx.VirtualizedFXGrid
import solve.scene.view.virtualizedfx.VirtualizedFXGridProvider
import solve.testMemoryLeak
import solve.utils.Storage
import solve.utils.structures.Size
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class VirtualizedFXGridTests : InteractiveTestClass() {
    private data class TestGridData(val name: String)
    private class TestGridCell(private var data: TestGridData) : GridCell<TestGridData> {
        var disposeCount = 0

        override fun getNode(): Node {
            return Rectangle()
        }

        override fun updateItem(data: TestGridData) {
            this.data = data
        }

        override fun dispose() {
            super.dispose()
            disposeCount++
        }
    }

    private var columnsNumber = 10
    private lateinit var data: List<TestGridData>
    private lateinit var gridData: ObservableGrid<TestGridData>
    private lateinit var virtualGrid: VirtualGrid<TestGridData, TestGridCell>
    private lateinit var vsp: VirtualScrollPane
    private lateinit var scale: DoubleProperty
    private lateinit var cellSize: Size
    private lateinit var virtualizedFXGrid: VirtualizedFXGrid
    private lateinit var createdCells: MutableList<TestGridCell>

    @BeforeEach
    fun setUp() {
        scale = SimpleDoubleProperty(1.0)
        cellSize = Size(200.0, 100.0)
        columnsNumber = 10
        createdCells = mutableListOf()
        data = (0 until 50).map { TestGridData(it.toString()) }
        gridData = ObservableGrid.fromList(data, columnsNumber)
        virtualGrid = VirtualGrid(gridData) {
            val cell = TestGridCell(it)
            createdCells.add(cell)
            cell
        }
        virtualGrid.cellSize = io.github.palexdev.mfxcore.base.beans.Size(cellSize.width, cellSize.height)
        vsp = VSPUtils.wrap(virtualGrid)
        virtualizedFXGrid = VirtualizedFXGrid(virtualGrid, vsp, scale, cellSize)
    }

    @Test
    fun `Correct cell size if initial scale is not default`() {
        scale.value *= 1.5
        virtualizedFXGrid = VirtualizedFXGrid(virtualGrid, vsp, scale, cellSize)
        assertEquals(cellSize.height * scale.value, virtualGrid.cellSize.height)
        assertEquals(cellSize.width * scale.value, virtualGrid.cellSize.width)
    }

    @Test
    fun `Updates cell size when scale changed`() {
        scale.value *= 1.5
        assertEquals(cellSize.height * scale.value, virtualGrid.cellSize.height)
        assertEquals(cellSize.width * scale.value, virtualGrid.cellSize.width)

        scale.value /= 4
        assertEquals(cellSize.height * scale.value, virtualGrid.cellSize.height)
        assertEquals(cellSize.width * scale.value, virtualGrid.cellSize.width)
    }

    @Test
    fun `Updates position when virtual grid was scrolled`() {
        assertEquals(0.0, virtualizedFXGrid.xProperty.value)
        assertEquals(0.0, virtualizedFXGrid.yProperty.value)
        val newX = 100.0
        val newY = 200.0
        virtualGrid.scrollTo(newX, Orientation.HORIZONTAL)
        virtualGrid.scrollTo(newY, Orientation.VERTICAL)
        assertEquals(newX, virtualizedFXGrid.xProperty.value)
        assertEquals(newY, virtualizedFXGrid.yProperty.value)
    }

    @Test
    fun `Change columns number`() {
        columnsNumber = 4
        virtualizedFXGrid.changeColumnsNumber(columnsNumber)
        assertEquals(columnsNumber, virtualGrid.columnsNum)
        assertEquals(data.size / columnsNumber + 1, virtualGrid.rowsNum)
    }

    @Test
    fun `Scroll methods`() {
        val newX = 100.0
        val newY = 100.0
        virtualizedFXGrid.scrollX(newX)
        virtualizedFXGrid.scrollY(newY)
        assertEquals(newX, virtualGrid.position.x)
        assertEquals(newY, virtualGrid.position.y)
    }

    @Test
    fun `Scroll outside borders`() {
        val maxX = columnsNumber * cellSize.width
        val maxY = (data.size / columnsNumber) * cellSize.height
        val returnedX = virtualizedFXGrid.scrollX(100000.0)
        val returnedY = virtualizedFXGrid.scrollY(100000.0)
        assertEquals(maxX, virtualGrid.position.x)
        assertEquals(maxY, virtualGrid.position.y)
        assertEquals(maxX, returnedX)
        assertEquals(maxY, returnedY)
    }

    @Test
    fun `Panning test`(robot: FxRobot) {
        virtualizedFXGrid.setUpPanning()
        val startX = 250.0
        val startY = 430.0
        val finishX = 100.0
        val finishY = 143.0
        robot.interact {
            virtualGrid.fireMousePressed(startX, startY, MouseButton.PRIMARY)
            virtualGrid.fireMouseDragged(finishX, finishY, MouseButton.PRIMARY)
        }
        assertEquals(startX - finishX, virtualGrid.position.x)
        assertEquals(startY - finishY, virtualGrid.position.y)
    }

    @Test
    fun `No panning on right button`(robot: FxRobot) {
        virtualizedFXGrid.setUpPanning()
        val startX = 250.0
        val startY = 430.0
        val finishX = 100.0
        val finishY = 143.0
        robot.interact {
            virtualGrid.fireMousePressed(startX, startY, MouseButton.SECONDARY)
            virtualGrid.fireMouseDragged(finishX, finishY, MouseButton.PRIMARY)
        }
        assertEquals(0.0, virtualGrid.position.x)
        assertEquals(0.0, virtualGrid.position.y)
    }

    @Test
    fun `Add handler on mouse wheel`(robot: FxRobot) {
        val scrollEvents = mutableListOf<ScrollEvent>()
        virtualizedFXGrid.setOnMouseWheel {
            scrollEvents.add(it)
        }
        val x1 = 100.0
        val x2 = 140.0
        robot.interact {
            virtualGrid.fireScrollEvent(x1, 20.0)
            virtualGrid.fireScrollEvent(x2, 20.0)
        }
        assertEquals(2, scrollEvents.size)
        assertEquals(x1, scrollEvents[0].x)
        assertEquals(x2, scrollEvents[1].x)
    }

    @Test
    fun `No scroll on mouse wheel`(robot: FxRobot) {
        robot.interact {
            vsp.fireScrollEvent(10.0, 20.0)
            virtualGrid.fireScrollEvent(10.0, 20.0)
        }
        assertEquals(0.0, virtualGrid.position.x)
        assertEquals(0.0, virtualGrid.position.y)
    }

    @Test
    fun `Grid can be garbage collected after dispose`() {
        val factory = { virtualizedFXGrid }
        testMemoryLeak(factory) { grid ->
            grid.dispose()
            setUp()
        }
    }

    @Test
    fun `VirtualizedFX grid provider creates grid correctly`() {
        val image = WritableImage(100, 200)
        val framesCount = 100
        val columnsNumber = 3
        val outOfFramesLayer = OutOfFramesLayer()
        val data = (0 until framesCount).map { VisualizationFrame(it.toDouble(), { image }, listOf()) }
        val scene = solve.scene.model.Scene(listOf(), listOf())
        val cache = TestStorage()
        val associationsManager = AssociationsManager<VisualizationFrame, Landmark.Keypoint>(
            cellSize,
            10.0,
            scale,
            listOf(),
            SimpleIntegerProperty(columnsNumber),
            outOfFramesLayer
        )
        val parameters = FrameViewParameters(CoroutineScope(Dispatchers.Default), associationsManager, scene)
        val grid = VirtualizedFXGridProvider.createGrid(data, columnsNumber, cellSize, scale, outOfFramesLayer) {
            FrameView(cellSize, scale, cache, 0, parameters, it)
        }

        val createdVsp = grid.node as VirtualScrollPane
        val createdGrid = createdVsp.content as VirtualGrid<*, *>

        assertEquals(3, createdGrid.columnsNum)
        assertEquals(framesCount / columnsNumber + 1, createdGrid.rowsNum)
        assertEquals(cellSize.height, createdGrid.cellSize.height)
        assertEquals(cellSize.width, createdGrid.cellSize.width)
    }

    private class TestStorage : Storage<FrameView> {
        override fun store(element: FrameView) {
        }
    }
}
