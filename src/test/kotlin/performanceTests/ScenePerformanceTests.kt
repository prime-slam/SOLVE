package performanceTests

import com.sun.javafx.perf.PerformanceTracker
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import solve.importer.FullParserForImport
import solve.importer.ProjectParser
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.scene.view.SceneView
import tornadofx.App
import tornadofx.find
import tornadofx.launch
import kotlin.math.abs

abstract class ScenePerformanceTestApp : App(SceneView::class) {
    protected val sceneController = find<SceneController>()
    protected val sceneView = find<SceneView>()
    private val sceneFacade = SceneFacade(sceneController)

    override fun start(stage: Stage) {
        with(stage) {
            width = 1000.0
            height = 600.0
            isMaximized = true
        }
        super.start(stage)

        val path = parameters.unnamed[0]
        val times = parameters.unnamed[1].toInt()

        val project = ProjectParser.partialParseDirectory(path) ?: fail("Couldn't parse test project")
        val parsedProject = FullParserForImport.fullParseDirectory(project)
        sceneFacade.visualize(parsedProject.layers, parsedProject.frames)

        testMethod(times)
    }

    protected abstract fun testMethod(times: Int)
}

class ScrollingPerformanceTestApp : ScenePerformanceTestApp() {
    override fun testMethod(times: Int) {
        val performanceTracker = PerformanceTracker.getSceneTracker(sceneView.root.scene)
        var scrolledCounter = 0
        var fpsCounter = 0.0
        val timer: AnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (sceneController.x == 0.0) {
                    if (scrolledCounter == times) {
                        ScenePerformanceTestsResults.avgHorizontalScrollingFPS = fpsCounter / times
                        Platform.exit()
                    }
                    performanceTracker.resetAverageFPS()
                }
                val initialX = sceneController.x
                sceneController.x += scrollingSpeed
                val x = sceneController.x
                if (abs(x - (initialX + scrollingSpeed)) > 0.01) {
                    sceneController.x = 0.0
                    scrolledCounter++;
                    println(performanceTracker.averageFPS)
                    fpsCounter += performanceTracker.averageFPS
                }
            }
        }

        timer.start()
    }

    companion object {
        const val scrollingSpeed = 20.0
    }
}

class ScenePerformanceTests {
    @Test
    fun scrollingTest() {
        launch<ScrollingPerformanceTestApp>(
            arrayOf(testAppPath, runMethodTimes.toString())
        )
        println(ScenePerformanceTestsResults.avgHorizontalScrollingFPS)
        Assertions.assertFalse(ScenePerformanceTestsResults.avgHorizontalScrollingFPS < minFPS)
    }

    companion object {
        const val testAppPath = "testData/TestProject2"
        const val runMethodTimes = 2
        const val minFPS = 30.0

    }

}

object ScenePerformanceTestsResults {
    var avgHorizontalScrollingFPS: Double = 0.0
}
