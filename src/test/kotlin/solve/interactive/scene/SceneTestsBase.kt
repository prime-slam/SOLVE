package solve.interactive.scene

import io.github.palexdev.materialfx.utils.SwingFXUtils
import javafx.scene.image.WritableImage
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.io.TempDir
import solve.interactive.InteractiveTestClass
import solve.parsers.lines.createCSVFileWithData
import solve.parsers.structures.Point
import solve.project.model.LandmarkFile
import solve.project.model.LayerKind
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import solve.scene.controller.SceneController
import solve.scene.model.LayerState
import tornadofx.*
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.min

internal open class SceneTestsBase : InteractiveTestClass() {
    protected val controller = find<SceneController>()

    protected fun getState(frameNumber: Int, layerName: String, landmark: Long): LayerState {
        return controller.scene.frames[frameNumber].layers.single { it.name == layerName }.getLandmarks()
            .single { it.uid == landmark }.layerState
    }

    protected fun createScene(
        layersName: List<String>,
        projectName: String = "project1",
        framesCount: Int = 10
    ): SceneData {
        val layers = layersName.map { ProjectLayer(LayerKind.Keypoint, it, projectName) }
        val frames = (0 until framesCount).map { i ->
            val files = layers.map { LandmarkFile(it, keypointFiles[min(i, 9)].toPath(), listOf(i.toLong())) }
            ProjectFrame(
                i.toLong(),
                imageFiles[min(i, 9)].toPath(),
                files
            )
        }
        return SceneData(layers, frames)
    }

    protected data class SceneData(val layers: List<ProjectLayer>, val frames: List<ProjectFrame>)

    companion object {
        @TempDir
        lateinit var tempFolder: File

        const val framesCount = 10
        const val width = 100
        const val height = 200
        const val indent = 10
        lateinit var imageFiles: List<File>
        lateinit var keypointFiles: List<File>

        @JvmStatic
        @BeforeAll
        fun setUpAll() {
            val uidsRange = 0 until framesCount
            keypointFiles =
                uidsRange.map { createCSVFileWithData(tempFolder, csvTestPointsStringData, "$it.csv") }
            imageFiles = uidsRange.map { createImage(tempFolder, "$it.png") }
        }

        private fun createImage(tempFolder: File, name: String): File {
            val image = WritableImage(width, height)
            val imageFile = File(tempFolder, name)
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", imageFile)
            return imageFile
        }

        private const val CSVPointDataStringPrefix = "uid,x,y\n"
        val testPoints = listOf(
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
