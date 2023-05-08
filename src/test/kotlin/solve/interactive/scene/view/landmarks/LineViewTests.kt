package solve.interactive.scene.view.landmarks

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javafx.animation.StrokeTransition
import javafx.animation.Timeline
import javafx.scene.input.MouseButton
import javafx.scene.shape.Line
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import solve.fireMouseEntered
import solve.fireMouseExited
import solve.interactive.InteractiveTestClass
import solve.scene.model.ColorManager
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.LayerState
import solve.scene.model.Point
import solve.scene.view.landmarks.AnimationProvider
import solve.scene.view.landmarks.LineView
import solve.testMemoryLeak
import solve.utils.ServiceLocator
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class LineViewTests : InteractiveTestClass() {
    private val layerName = "lines"
    private lateinit var colorManager: ColorManager<String>
    private lateinit var layerSettings: LayerSettings.LineLayerSettings
    private lateinit var layerState: LayerState
    private lateinit var line: Landmark.Line
    private lateinit var lineView: LineView
    private var scale = 1.0
    private var viewOrder = 0

    @BeforeEach
    fun setUp() {
        colorManager = ColorManager()
        layerSettings = LayerSettings.LineLayerSettings(layerName, layerName, colorManager)
        layerState = LayerState(layerName)
        val startPoint = Point(10, 20)
        val finishPoint = Point(20, 45)
        line = Landmark.Line(1, layerSettings, layerState, startPoint, finishPoint)
        scale = 1.0
        viewOrder = 0
        lineView = LineView(line, viewOrder, scale)
    }

    @Test
    fun `Change view order`() {
        val nodeViewOrder = lineView.node.viewOrder
        lineView.viewOrder = 3
        Assertions.assertTrue(nodeViewOrder < lineView.node.viewOrder, "Landmark with less view order is above")
    }

    @Test
    fun `Correct position of landmark`() {
        Assertions.assertEquals(line.startCoordinate.x * scale, lineView.node.startX)
        Assertions.assertEquals(line.startCoordinate.y * scale, lineView.node.startY)
        Assertions.assertEquals(line.finishCoordinate.x * scale, lineView.node.endX)
        Assertions.assertEquals(line.finishCoordinate.y * scale, lineView.node.endY)
    }

    @Test
    fun `Updates position when scale changed`() {
        lineView.scale = 2.0
        lineView.scale = scale
        Assertions.assertEquals(line.startCoordinate.x * scale, lineView.node.startX)
        Assertions.assertEquals(line.startCoordinate.y * scale, lineView.node.startY)
        Assertions.assertEquals(line.finishCoordinate.x * scale, lineView.node.endX)
        Assertions.assertEquals(line.finishCoordinate.y * scale, lineView.node.endY)
        scale = 0.3
        lineView.scale = scale
        Assertions.assertEquals(line.startCoordinate.x * scale, lineView.node.startX)
        Assertions.assertEquals(line.startCoordinate.y * scale, lineView.node.startY)
        Assertions.assertEquals(line.finishCoordinate.x * scale, lineView.node.endX)
        Assertions.assertEquals(line.finishCoordinate.y * scale, lineView.node.endY)
    }

    @Test
    fun `Use common color changed`() {
        layerSettings.useCommonColor = true
        Assertions.assertEquals(layerSettings.commonColor, lineView.node.stroke)
        layerSettings.useCommonColor = false
        Assertions.assertEquals(layerSettings.getUniqueColor(line), lineView.node.stroke)
    }

    @Test
    fun `Common color changed`() {
        layerSettings.useCommonColor = true
        val newColor = c("FACFAF")
        layerSettings.commonColor = newColor
        Assertions.assertEquals(newColor, lineView.node.stroke)
    }

    @Test
    fun `Highlight on mouse enter`(robot: FxRobot) {
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider) {
            robot.interact {
                lineView.node.fireMouseEntered(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Unhighlight on mouse exit`(robot: FxRobot) {
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider) {
            robot.interact {
                lineView.node.fireMouseEntered(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(animationProvider) {
            robot.interact {
                lineView.node.fireMouseExited(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Can be garbage collected after dispose`() {
        val factory = { lineView }
        testMemoryLeak(factory) {
            it.dispose()
            lineView = LineView(line, viewOrder, scale)
        }
    }

    @Test
    fun `Width changed`() {
        Assertions.assertEquals(layerSettings.selectedWidth, lineView.node.strokeWidth)
        layerSettings.selectedWidth *= 2
        Assertions.assertEquals(layerSettings.selectedWidth, lineView.node.strokeWidth)
    }

    @Test
    fun `Enabled changed`() {
        Assertions.assertEquals(layerSettings.enabled, lineView.node.isVisible)
        layerSettings.enabled = !layerSettings.enabled
        Assertions.assertEquals(layerSettings.enabled, lineView.node.isVisible)
    }

    private data class TestParameter(val shape: Line, val times: Int)

    private fun testHighlighted(
        animationProvider: AnimationProvider,
        params: List<TestParameter> = listOf(TestParameter(lineView.node, 1)),
        action: () -> Unit
    ) {
        params.forEach { param ->
            every {
                animationProvider.createStrokeTransition(
                    param.shape,
                    layerSettings.getUniqueColor(line),
                    allAny()
                )
            } answers { StrokeTransition() }
            every {
                animationProvider.createWidthTransition(
                    param.shape,
                    layerSettings.selectedWidth * 2,
                    allAny()
                )
            } answers { Timeline() }
        }

        action()

        params.forEach { param ->
            verify(exactly = param.times) {
                animationProvider.createStrokeTransition(
                    param.shape,
                    layerSettings.getUniqueColor(line),
                    allAny()
                )
            }
            verify(exactly = param.times) {
                animationProvider.createWidthTransition(
                    param.shape,
                    layerSettings.selectedWidth * 2,
                    allAny()
                )
            }
        }
    }

    private fun testUnhighlighted(
        animationProvider: AnimationProvider,
        params: List<TestParameter> = listOf(TestParameter(lineView.node, 1)),
        action: () -> Unit
    ) {
        params.forEach { param ->
            every {
                animationProvider.createStrokeTransition(
                    param.shape,
                    layerSettings.getColor(line),
                    allAny()
                )
            } answers { StrokeTransition() }
            every {
                animationProvider.createWidthTransition(
                    param.shape,
                    layerSettings.selectedWidth,
                    allAny()
                )
            } answers { Timeline() }
        }

        action()

        params.forEach { param ->
            verify(exactly = param.times) {
                animationProvider.createStrokeTransition(
                    param.shape,
                    layerSettings.getColor(line),
                    allAny()
                )
            }
            verify(exactly = param.times) {
                animationProvider.createWidthTransition(
                    param.shape,
                    layerSettings.selectedWidth,
                    allAny()
                )
            }
        }
    }
}
