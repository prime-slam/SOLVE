package solve.performance

import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import solve.importer.FullParserForImport
import solve.importer.ProjectParser
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.scene.view.SceneView
import solve.utils.structures.DoublePoint
import tornadofx.*
import java.util.stream.Stream
import kotlin.math.pow

@ExtendWith(ApplicationExtension::class)
@Disabled("Disabled until bug #2019 has been fixed!")
internal class ScenePerformanceTests {
    companion object {
        const val keypointsTestProject = "testData/TestProject2"
        const val linesTestProject = "testData/LinesProject"
        const val planesTestProject = "testData/PlanesProject"
        const val frameRate = 15
        const val oneSceneRunCount = 10
        const val scenesCount = 15

        var keypointsZoomingResults = mutableListOf<Double>()
        var linesZoomingResults = mutableListOf<Double>()
        var planesZoomingResults = mutableListOf<Double>()

        var keypointsScrollingResults = mutableListOf<Double>()
        var linesScrollingResults = mutableListOf<Double>()
        var planesScrollingResults = mutableListOf<Double>()

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            println(
                "Keypoints zooming: ${keypointsZoomingResults.average()} +- ${getVariance(keypointsZoomingResults)}"
            )
            println("Lines zooming: ${linesZoomingResults.average()} +- ${getVariance(linesZoomingResults)}")
            println("Planes zooming: ${planesZoomingResults.average()} +- ${getVariance(planesZoomingResults)}")

            println(
                "Keypoints scrolling: ${keypointsScrollingResults.average()}" +
                    "+- ${getVariance(keypointsScrollingResults)}"
            )
            println("Lines scrolling: ${linesScrollingResults.average()} +- ${getVariance(linesScrollingResults)}")
            println("Planes scrolling: ${planesScrollingResults.average()} +- ${getVariance(planesScrollingResults)}")
        }

        private fun getVariance(results: List<Double>): Double {
            val avg = results.average()
            return results.sumOf { (it - avg).pow(2.0) } / scenesCount
        }
    }

    private val controller = find<SceneController>()
    private lateinit var scene: Scene
    private lateinit var label: Label

    @Start
    fun start(stage: Stage) {
        val sceneView = find<SceneView>()
        label = Label("0")
        scene = Scene(StackPane(sceneView.root, label), 1920.0, 1080.0)
        stage.scene = scene
        stage.show()
    }

    @ParameterizedTest
    @MethodSource("solve.performance.ProjectPaths#projectPaths")
    fun zoomingTest(path: String, robot: FxRobot) {
        robot.interact {
            visualizeProject(path)
        }
        robot.interact {
            controller.scale = SceneController.DefaultMaxScale
        }
        var counter = 0
        var isPositive = false
        var prevScale = controller.scale
        val point = DoublePoint(0.0, 0.0)
        val secondaryAction = {
            counter++
            label.text = counter.toString()
        }
        val resetAction = { isPositive = !isPositive }
        val isRunFinished = { controller.scale == prevScale }
        val action = {
            if (isPositive) {
                controller.zoomIn(point)
            } else {
                controller.zoomOut(point)
            }
        }
        val afterAction = { prevScale = controller.scale }
        val avgFps = getAvgFps(
            oneSceneRunCount,
            frameRate,
            scene,
            secondaryAction,
            action,
            isRunFinished,
            resetAction,
            afterAction
        )

        if (path == keypointsTestProject) {
            keypointsZoomingResults.add(avgFps)
        }
        if (path == linesTestProject) {
            linesZoomingResults.add(avgFps)
        }
        if (path == planesTestProject) {
            planesZoomingResults.add(avgFps)
        }
    }

    @ParameterizedTest
    @MethodSource("solve.performance.ProjectPaths#projectPaths")
    fun scrollingTest(path: String, robot: FxRobot) {
        robot.interact {
            visualizeProject(path)
        }
        robot.interact {
            controller.scale = 0.6
        }
        var counter = 0
        var isPositive = false
        var prevPosition = controller.y
        val secondaryAction = {
            counter++
            label.text = counter.toString()
        }
        val resetAction = { isPositive = !isPositive }
        val isRunFinished = { controller.y == prevPosition }
        val speed = 50
        val action: () -> Unit = {
            if (isPositive) {
                controller.scrollY!!.invoke(controller.y + speed)
            } else {
                controller.scrollY!!.invoke(controller.y - speed)
            }
        }
        val afterAction = { prevPosition = controller.y }
        val avgFps = getAvgFps(
            oneSceneRunCount,
            frameRate,
            scene,
            secondaryAction,
            action,
            isRunFinished,
            resetAction,
            afterAction
        )

        if (path == keypointsTestProject) {
            keypointsScrollingResults.add(avgFps)
        }
        if (path == linesTestProject) {
            linesScrollingResults.add(avgFps)
        }
        if (path == planesTestProject) {
            planesScrollingResults.add(avgFps)
        }
    }

    private fun visualizeProject(path: String) {
        val partialParsedDirectory = ProjectParser.partialParseDirectory(path)!!
        val parsedDirectory = FullParserForImport.fullParseDirectory(partialParsedDirectory)
        SceneFacade.visualize(parsedDirectory.layers, parsedDirectory.frames, false)
    }
}

class ProjectPaths {
    companion object {
        @JvmStatic
        fun projectPaths(): Stream<String> {
            val keypoints = (1..ScenePerformanceTests.scenesCount).map { ScenePerformanceTests.keypointsTestProject }
            val lines = (1..ScenePerformanceTests.scenesCount).map { ScenePerformanceTests.linesTestProject }
            val planes = (1..ScenePerformanceTests.scenesCount).map { ScenePerformanceTests.planesTestProject }
            return (keypoints + lines + planes).stream()
        }
    }
}
