package solve.interactive

import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import solve.fireMouseDragged
import solve.fireMousePressed
import solve.fireScrollEvent
import solve.interactive.scene.SceneTestsBase
import solve.scene.SceneFacade
import solve.scene.view.SceneView
import solve.scene.view.virtualizedfx.VirtualScrollPaneWithOutOfFramesLayer
import solve.utils.structures.DoublePoint
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class SceneViewTests : SceneTestsBase() {
    private val vsp: VirtualScrollPaneWithOutOfFramesLayer<*, *>
        get() {
            val scene = find<SceneView>()
            return scene.root.children.single() as VirtualScrollPaneWithOutOfFramesLayer<*, *>
        }

    private val grid
        get() = vsp.virtualGrid

    @BeforeEach
    fun setUp() {
        val sceneView = find<SceneView>()
        val data = createScene(listOf("layer1"), framesCount = 20)
        SceneFacade.visualize(data.layers, data.frames, false)
    }

    @Test
    fun `Controller updates position when grid scrolled`(robot: FxRobot) {
        robot.interact {
            val newX = 10.0
            val newY = 50.0
            grid.scrollTo(newX, Orientation.HORIZONTAL)
            grid.scrollTo(newY, Orientation.VERTICAL)
            assertEquals(newX, controller.x, 0.01)
            assertEquals(newY, controller.y, 0.01)
        }
    }

    @Test
    fun `Panning is set up`(robot: FxRobot) {
        robot.interact {
            val startX = 5.0
            val finishX = 2.5
            val startY = 15.0
            val finishY = 10.0
            grid.fireMousePressed(startX, startY, MouseButton.PRIMARY)
            grid.fireMouseDragged(finishX, finishY, MouseButton.PRIMARY)
            assertEquals(startX - finishX, controller.x, 0.01)
            assertEquals(startY - finishY, controller.y, 0.01)
        }
    }

    @Test
    fun `Zoom by mouse wheel`(robot: FxRobot) {
        robot.interact {
            val initialScale = controller.scale
            val point = DoublePoint(5.0, 10.0)
            grid.fireScrollEvent(point.x, point.y)
            val finalScale = controller.scale
            assertTrue(finalScale > initialScale)
        }
    }
}
