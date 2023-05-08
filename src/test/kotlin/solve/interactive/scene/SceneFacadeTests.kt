package solve.interactive.scene

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import solve.parsers.structures.Point
import solve.scene.SceneFacade
import solve.scene.model.Landmark
import solve.scene.model.Scene

internal class SceneFacadeTests : SceneTestsBase() {
    @BeforeEach
    fun setUp() {
        controller.setScene(Scene(emptyList(), emptyList()), false)
    }

    @Test
    fun `Visualize one layer`() {
        val layerName = "layer1"
        val data = createScene(listOf(layerName))
        SceneFacade.visualize(data.layers, data.frames, false)

        val scene = controller.scene

        assertEquals(framesCount, scene.frames.size)
        assertEquals(1, scene.layerSettings.size)

        val layersSettings = scene.layerSettings.single()
        assertEquals(layerName, layersSettings.layerName)

        val frameNumber = 5
        val frame = scene.frames[frameNumber]
        assertEquals(frameNumber.toLong(), frame.timestamp)
        assertEquals(1, frame.layers.size)

        val frameLayer = frame.layers.single()
        assertSame(layersSettings, frameLayer.settings)
        assertEquals(layerName, frameLayer.name)

        assertEquals(layersSettings, frame.layers.single().settings)

        val points = frameLayer.getLandmarks().filterIsInstance<Landmark.Keypoint>().map {
            Point(it.uid, it.coordinate.x.toDouble(), it.coordinate.y.toDouble())
        }

        assertEquals(testPoints.toSet(), points.toSet())
    }

    @Test
    fun `Visualize two layers`() {
        val layer1Name = "layer1"
        val layer2Name = "layer2"
        val data = createScene(listOf(layer1Name, layer2Name))
        SceneFacade.visualize(data.layers, data.frames, false)

        val scene = controller.scene

        assertEquals(framesCount, scene.frames.size)
        assertEquals(2, scene.layerSettings.size)
        val layer1Settings = assertDoesNotThrow { scene.layerSettings.single { it.layerName == layer1Name } }
        val layer2Settings = assertDoesNotThrow { scene.layerSettings.single { it.layerName == layer2Name } }

        val frameLayers = scene.frames[3].layers
        assertEquals(2, frameLayers.size)

        val frameLayer1 = frameLayers.single { it.name == layer1Name }
        val frameLayer2 = frameLayers.single { it.name == layer2Name }

        assertEquals(layer1Settings, frameLayer1.settings)
        assertEquals(layer2Settings, frameLayer2.settings)
    }

    @Test
    fun `Visualize same layer twice`() {
        val layerName = "layer1"
        val data1 = createScene(listOf(layerName))
        SceneFacade.visualize(data1.layers, data1.frames, false)
        val settings = controller.scene.layerSettings.single()
        val state = getState(0, layerName, 1)
        state.hoveredLandmarksUids.add(1)
        val data2 = createScene(listOf(layerName), framesCount = 5)
        SceneFacade.visualize(data2.layers, data2.frames, true)
        assertEquals(5, controller.scene.frames.size)
        assertSame(settings, controller.scene.layerSettings.single())
        val newState = getState(0, layerName, 1)
        assertNotSame(state, newState)
        assertEquals(0, newState.hoveredLandmarksUids.size)
    }

    @Test
    fun `Visualize different projects one after another`() {
        val layerName = "layer1"
        val data1 = createScene(listOf(layerName), "project1")
        val data2 = createScene(listOf(layerName), "project2")
        SceneFacade.visualize(data1.layers, data1.frames, false)
        val settings = controller.scene.layerSettings.single()
        settings.opacity -= 0.05
        SceneFacade.visualize(data2.layers, data2.frames, true)
        val newSettings = controller.scene.layerSettings.single()
        assertNotSame(settings, newSettings)
        assertNotEquals(settings.opacity, newSettings.opacity)
    }

    @Test
    fun `Empty project`() {
        SceneFacade.visualize(listOf(), listOf(), false)
        val scene = controller.scene
        assertEquals(0, scene.frames.size)
        assertEquals(0, scene.layerSettings.size)
    }
}
