package solve.unit.scene.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import solve.scene.SceneFacade
import solve.unit.scene.SceneTestsBase
import solve.utils.structures.DoublePoint

internal class SceneControllerTests : SceneTestsBase() {
    @BeforeEach
    fun setUp() {
        controller.sceneWidthProperty.value = 1000.0
        val data = createScene(listOf("layer1"), framesCount = 20)
        SceneFacade.visualize(data.layers, data.frames, false)
    }

    @Test
    fun `Initial columns number on two frames`() {
        val data = createScene(listOf("layer1"), framesCount = 2)
        SceneFacade.visualize(data.layers, data.frames, false)
        assertEquals(2, controller.columnsNumber)
    }

    @Test
    fun `Initial columns number on four frames`() {
        val data = createScene(listOf("layer1"), framesCount = 4)
        SceneFacade.visualize(data.layers, data.frames, false)
        assertEquals(2, controller.columnsNumber)
    }

    @Test
    fun `Initial columns number on many frames`() {
        assertEquals(5, controller.columnsNumber)
    }

    @Test
    fun `Zoom in`() {
        var newX = 0.0
        controller.scrollX = {
            newX = it
            it
        }
        var newY = 0.0
        controller.scrollY = {
            newY = it
            it
        }

        val initialScale = controller.scale
        val point = DoublePoint(350.0, 500.0)
        controller.zoomIn(point)
        assertTrue(controller.scale > initialScale)
        assertEquals(52.5, newX)
        assertEquals(75.0, newY)
    }

    @Test
    fun `Zoom out`() {
        var newX = 0.0
        controller.scrollX = {
            newX = it
            it
        }
        var newY = 0.0
        controller.scrollY = {
            newY = it
            it
        }
        val point = DoublePoint(0.0, 0.0)
        controller.zoomIn(point)
        val initialScale = controller.scale
        controller.zoomIn(point)
        controller.zoomOut(point)
        assertEquals(initialScale, controller.scale, 0.01)
        assertEquals(0.0, newX, 0.01)
        assertEquals(0.0, newY, 0.01)
    }

    @Test
    fun `Zoom out on min scale`() {
        var newX = 0.0
        controller.scrollX = {
            newX = it
            it
        }
        var newY = 0.0
        controller.scrollY = {
            newY = it
            it
        }
        val initialScale = controller.scale
        val point = DoublePoint(0.0, 0.0)
        controller.zoomOut(point)
        assertEquals(initialScale, controller.scale, 0.01)
        assertEquals(0.0, newX, 0.01)
        assertEquals(0.0, newY, 0.01)
    }

    @Test
    fun `Zoom in on max scale`() {
        var newX = 0.0
        controller.scrollX = {
            newX = it
            it
        }
        var newY = 0.0
        controller.scrollY = {
            newY = it
            it
        }
        val point = DoublePoint(0.0, 0.0)
        while (controller.scale != controller.installedMaxScale) {
            controller.zoomIn(point)
        }
        controller.zoomIn(point)
        assertEquals(controller.installedMaxScale, controller.scale, 0.01)
        assertEquals(0.0, newX, 0.01)
        assertEquals(0.0, newY, 0.01)
    }

    @Test
    fun `Scene width changed`() {
        val initialScale = controller.scale
        controller.sceneWidthProperty.value *= 2
        controller.recalculateScale(true)
        assertEquals(initialScale * 2, controller.scale)
    }
}
