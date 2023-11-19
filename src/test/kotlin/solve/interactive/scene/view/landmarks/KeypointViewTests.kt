package solve.interactive.scene.view.landmarks

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javafx.animation.FillTransition
import javafx.scene.input.MouseButton
import javafx.scene.shape.Shape
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import solve.fireMouseClicked
import solve.fireMouseEntered
import solve.fireMouseExited
import solve.interactive.InteractiveTestClass
import solve.scene.model.ColorManager
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.LayerState
import solve.scene.model.Point
import solve.scene.view.landmarks.KeypointView
import solve.testMemoryLeak
import solve.utils.ServiceLocator
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class KeypointViewTests : InteractiveTestClass() {
    private val layerName = "keypoints"
    private lateinit var colorManager: ColorManager<String>
    private lateinit var layerSettings: LayerSettings.PointLayerSettings
    private lateinit var layerState: LayerState
    private lateinit var keypoint: Landmark.Keypoint
    private lateinit var keypointView: KeypointView
    private var scale = 1.0
    private var viewOrder = 0

    @BeforeEach
    fun setUp() {
        colorManager = ColorManager()
        layerSettings = LayerSettings.PointLayerSettings(layerName, layerName, colorManager)
        layerState = LayerState(layerName)
        val point = Point(10, 20)
        keypoint = Landmark.Keypoint(1, layerSettings, layerState, point)
        scale = 1.0
        viewOrder = 0
        keypointView = KeypointView(keypoint, viewOrder, scale)
    }

    @Test
    fun `Change view order`() {
        val nodeViewOrder = keypointView.node.viewOrder
        keypointView.viewOrder = 3
        assertTrue(nodeViewOrder < keypointView.node.viewOrder, "Landmark with less view order is above")
    }

    @Test
    fun `Correct position of landmark`() {
        assertEquals(keypoint.coordinate.x * scale, keypointView.node.centerX)
        assertEquals(keypoint.coordinate.y * scale, keypointView.node.centerY)
    }

    @Test
    fun `Updates position when scale changed`() {
        scale = 2.0
        keypointView.scale = scale
        assertEquals(keypoint.coordinate.x * scale, keypointView.node.centerX)
        assertEquals(keypoint.coordinate.y * scale, keypointView.node.centerY)
        scale = 0.3
        keypointView.scale = scale
        assertEquals(keypoint.coordinate.x * scale, keypointView.node.centerX)
        assertEquals(keypoint.coordinate.y * scale, keypointView.node.centerY)
    }

    @Test
    fun `Use common color changed`() {
        layerSettings.useCommonColor = true
        assertEquals(layerSettings.commonColor, keypointView.node.fill)
        layerSettings.useCommonColor = false
        assertEquals(layerSettings.getUniqueColor(keypoint), keypointView.node.fill)
    }

    @Test
    fun `Common color changed`() {
        layerSettings.useCommonColor = true
        val newColor = c("FACFAF")
        layerSettings.commonColor = newColor
        assertEquals(newColor, keypointView.node.fill)
    }

    @Test
    fun `Highlight on mouse enter`(robot: FxRobot) {
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider) {
            robot.interact {
                keypointView.node.fireMouseEntered(0.0, 0.0, MouseButton.PRIMARY)
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
                keypointView.node.fireMouseEntered(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(animationProvider) {
            robot.interact {
                keypointView.node.fireMouseExited(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Keypoint stay highlighted after mouse click`(robot: FxRobot) {
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(animationProvider, listOf(TestParameter(keypointView.node, 0))) {
            robot.interact {
                keypointView.node.fireMouseExited(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Keypoint unhighlighted after second mouse click`(robot: FxRobot) {
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(animationProvider) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Highlight same points in the layer`(robot: FxRobot) {
        val secondKeypoint = Landmark.Keypoint(1, layerSettings, layerState, Point(104, 205))
        val secondKeypointView = KeypointView(secondKeypoint, 1, scale)
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(
            animationProvider,
            listOf(TestParameter(keypointView.node, 1), TestParameter(secondKeypointView.node, 1))
        ) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Unhighlight same points in the layer`(robot: FxRobot) {
        val secondKeypoint = Landmark.Keypoint(1, layerSettings, layerState, Point(104, 205))
        val secondKeypointView = KeypointView(secondKeypoint, 1, scale)
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(
            animationProvider,
            listOf(TestParameter(keypointView.node, 1), TestParameter(secondKeypointView.node, 1))
        ) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(
            animationProvider,
            listOf(TestParameter(keypointView.node, 1), TestParameter(secondKeypointView.node, 1))
        ) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Don't affect different points in the layer`(robot: FxRobot) {
        val secondKeypoint = Landmark.Keypoint(2, layerSettings, layerState, Point(104, 205))
        val secondKeypointView = KeypointView(secondKeypoint, 1, scale)
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(
            animationProvider,
            listOf(TestParameter(keypointView.node, 1), TestParameter(secondKeypointView.node, 0))
        ) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(
            animationProvider,
            listOf(TestParameter(keypointView.node, 1), TestParameter(secondKeypointView.node, 0))
        ) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Don't highlight points in another layer`(robot: FxRobot) {
        val secondLayerName = "anotherLayer"
        val secondLayerSettings = LayerSettings.PointLayerSettings(secondLayerName, secondLayerName, ColorManager())
        val secondKeypoint = Landmark.Keypoint(1, secondLayerSettings, LayerState(secondLayerName), Point(104, 205))
        val secondKeypointView = KeypointView(secondKeypoint, 1, scale)
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(
            animationProvider,
            listOf(TestParameter(keypointView.node, 1), TestParameter(secondKeypointView.node, 0))
        ) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(
            animationProvider,
            listOf(TestParameter(keypointView.node, 1), TestParameter(secondKeypointView.node, 0))
        ) {
            robot.interact {
                keypointView.node.fireMouseClicked(0.0, 0.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Can be garbage collected after dispose`() {
        val factory = { keypointView }
        testMemoryLeak(factory) {
            it.dispose()
            keypointView = KeypointView(keypoint, viewOrder, scale)
        }
    }

    @Test
    fun `Radius changed`() {
        assertEquals(layerSettings.selectedRadius, keypointView.node.radiusX)
        assertEquals(layerSettings.selectedRadius, keypointView.node.radiusY)
        layerSettings.selectedRadius *= 2
        assertEquals(layerSettings.selectedRadius, keypointView.node.radiusX)
        assertEquals(layerSettings.selectedRadius, keypointView.node.radiusY)
    }

    @Test
    fun `Enabled changed`() {
        assertEquals(layerSettings.enabled, keypointView.node.isVisible)
        layerSettings.enabled = !layerSettings.enabled
        assertEquals(layerSettings.enabled, keypointView.node.isVisible)
    }

    private data class TestParameter(val shape: Shape, val times: Int)

    private fun testHighlighted(
        animationProvider: AnimationProvider,
        params: List<TestParameter> = listOf(TestParameter(keypointView.node, 1)),
        action: () -> Unit
    ) {
        params.forEach { param ->
            every {
                animationProvider.createFillTransition(
                    param.shape,
                    layerSettings.getUniqueColor(keypoint),
                    allAny()
                )
            } answers { FillTransition() }
            every {
                animationProvider.createScaleTransition(
                    param.shape,
                    2.0,
                    2.0,
                    allAny()
                )
            } answers { FillTransition() }
        }

        action()

        params.forEach { param ->
            verify(exactly = param.times) {
                animationProvider.createFillTransition(
                    param.shape,
                    layerSettings.getUniqueColor(keypoint),
                    allAny()
                )
            }
            verify(exactly = param.times) { animationProvider.createScaleTransition(param.shape, 2.0, 2.0, allAny()) }
        }
    }

    private fun testUnhighlighted(
        animationProvider: AnimationProvider,
        params: List<TestParameter> = listOf(TestParameter(keypointView.node, 1)),
        action: () -> Unit
    ) {
        params.forEach { param ->
            every {
                animationProvider.createFillTransition(
                    param.shape,
                    layerSettings.getColor(keypoint),
                    allAny()
                )
            } answers { FillTransition() }
            every {
                animationProvider.createScaleTransition(
                    param.shape,
                    1.0,
                    1.0,
                    allAny()
                )
            } answers { FillTransition() }
        }

        action()

        params.forEach { param ->
            verify(exactly = param.times) {
                animationProvider.createFillTransition(
                    param.shape,
                    layerSettings.getColor(keypoint),
                    allAny()
                )
            }
            verify(exactly = param.times) { animationProvider.createScaleTransition(param.shape, 1.0, 1.0, allAny()) }
        }
    }
}
