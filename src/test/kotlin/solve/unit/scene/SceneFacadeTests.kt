package solve.unit.scene

import io.github.palexdev.materialfx.utils.SwingFXUtils
import javafx.scene.image.WritableImage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import solve.parsers.lines.createCSVFileWithData
import solve.parsers.structures.Point
import solve.project.model.LandmarkFile
import solve.project.model.LayerKind
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.scene.model.Landmark
import solve.scene.model.LayerState
import solve.scene.model.Scene
import tornadofx.*
import java.io.File
import javax.imageio.ImageIO

internal class SceneFacadeTests {
    private val controller = find<SceneController>()

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

    private fun getState(frameNumber: Int, layerName: String, landmark: Long): LayerState {
        return controller.scene.frames[frameNumber].layers.single { it.name == layerName }.getLandmarks()
            .single { it.uid == landmark }.layerState
    }

    private fun createScene(
        layersName: List<String>,
        projectName: String = "project1",
        framesCount: Int = 10
    ): SceneData {
        val layers = layersName.map { ProjectLayer(LayerKind.Keypoint, it, projectName) }
        val frames = (0 until framesCount).map { i ->
            val files = layers.map { LandmarkFile(it, keypointFiles[i].toPath(), listOf(i.toLong())) }
            ProjectFrame(
                i.toLong(),
                imageFiles[i].toPath(),
                files
            )
        }
        return SceneData(layers, frames)
    }

    private data class SceneData(val layers: List<ProjectLayer>, val frames: List<ProjectFrame>)

    companion object {
        @TempDir
        lateinit var tempFolder: File

        private const val framesCount = 10
        private lateinit var imageFiles: List<File>
        private lateinit var keypointFiles: List<File>

        @JvmStatic
        @BeforeAll
        fun setUpAll() {
            val uidsRange = 0 until framesCount
            keypointFiles =
                uidsRange.map { createCSVFileWithData(tempFolder, csvTestPointsStringData, "$it.csv") }
            imageFiles = uidsRange.map { createImage(tempFolder, "$it.png") }
        }

        private fun createImage(tempFolder: File, name: String): File {
            val image = WritableImage(100, 200)
            val imageFile = File(tempFolder, name)
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", imageFile)
            return imageFile
        }

        private const val CSVPointDataStringPrefix = "uid,x,y\n"
        private val testPoints = listOf(
            Point(1, 5.0, 7.0),
            Point(2, 8.0, -9.0),
            Point(3, 5.0, -7.0),
            Point(5, 2.0, 0.0),
            Point(4, 1.0, 3.0),
            Point(8, 5.0, 14.0),
            Point(9, 3.0, 5.0)
        )
        private val csvTestPointsStringData =
            testPoints.joinToString(prefix = CSVPointDataStringPrefix, separator = "\n") { it.getCSVDataString() }

        private fun Point.getCSVDataString() = "$uid,$x,$y"
    }
}
