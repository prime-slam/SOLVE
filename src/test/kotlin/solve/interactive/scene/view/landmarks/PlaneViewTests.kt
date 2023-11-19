package solve.interactive.scene.view.landmarks

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javafx.animation.Timeline
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import solve.fireMousePressed
import solve.fireMouseReleased
import solve.interactive.InteractiveTestClass
import solve.scene.model.ColorManager
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.LayerState
import solve.scene.model.Point
import solve.scene.view.drawing.FrameEventManager
import solve.scene.view.landmarks.PlaneView
import solve.testMemoryLeak
import solve.utils.ServiceLocator
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class PlaneViewTests : InteractiveTestClass() {
    private val layerName = "planes"
    private lateinit var colorManager: ColorManager<String>
    private lateinit var layerSettings: LayerSettings.PlaneLayerSettings
    private lateinit var layerState: LayerState
    private lateinit var plane: Landmark.Plane
    private lateinit var planeView: PlaneView
    private lateinit var scale: DoubleProperty
    private lateinit var points: List<Point>
    private var viewOrder = 0
    private val width = 4.0
    private val height = 4.0
    private lateinit var bufferedImageView: BufferedImageView
    private lateinit var frameDrawer: FrameDrawer
    private lateinit var eventManager: FrameEventManager

    @BeforeEach
    fun setUp(robot: FxRobot) {
        scale = SimpleDoubleProperty(1.0)
        bufferedImageView = BufferedImageView(width, height, scale.value)
        frameDrawer = FrameDrawer(bufferedImageView, 3)
        eventManager = FrameEventManager(bufferedImageView, scale)
        colorManager = ColorManager()
        layerSettings = LayerSettings.PlaneLayerSettings(layerName, layerName, colorManager)
        layerState = LayerState(layerName)
        points = listOf(Point(1, 1), Point(1, 2))
        plane = Landmark.Plane(1, layerSettings, layerState, points)
        viewOrder = 0
        planeView = PlaneView(plane, frameDrawer, eventManager, viewOrder, scale.value)
        planeView.addToFrameDrawer()
        robot.interact {
            frameDrawer.fullRedraw()
        }
    }

    @Test
    fun `Use common color changed`(robot: FxRobot) {
        layerSettings.useCommonColor = false
        robot.interact {
            frameDrawer.fullRedraw()
        }
        assertColor(layerSettings.getUniqueColor(plane))
        layerSettings.useCommonColor = true
        robot.interact {
            frameDrawer.fullRedraw()
        }
        assertColor(layerSettings.getColor(plane))
    }

    @Test
    fun `Enabled changed`(robot: FxRobot) {
        robot.interact {
            layerSettings.enabled = false
            assertColor(Color.TRANSPARENT)
            layerSettings.enabled = true
            assertColor(layerSettings.getColor(plane))
        }
    }

    @Test
    fun `Common color changed`(robot: FxRobot) {
        layerSettings.useCommonColor = true
        val newColor = c("F0F0F0")
        layerSettings.commonColor = newColor
        robot.interact {
            frameDrawer.fullRedraw()
        }
        assertColor(newColor)
    }

    @Test
    fun `Scale changed`() {
        planeView.scale *= 2
        assertColor(layerSettings.getColor(plane))
    }

    @Test
    fun `View order changed`(robot: FxRobot) {
        val secondPlanePoints = listOf(Point(1, 0), Point(1, 2))
        val secondPlane = Landmark.Plane(2, layerSettings, layerState, secondPlanePoints)
        val secondPlaneView = PlaneView(secondPlane, frameDrawer, eventManager, 1, scale.value)
        robot.interact {
            secondPlaneView.addToFrameDrawer()
            frameDrawer.fullRedraw()
            assertColor(layerSettings.getColor(secondPlane), secondPlanePoints)
            planeView.viewOrder = 1
            secondPlaneView.viewOrder = 0
            planeView.addToFrameDrawer()
            secondPlaneView.addToFrameDrawer()
            frameDrawer.fullRedraw()
            assertColor(layerSettings.getColor(plane))
        }
    }

    @Test
    fun `Highlight plane on click`(robot: FxRobot) {
        layerSettings.useCommonColor = false
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider, 1) {
            robot.interact {
                bufferedImageView.fireMousePressed(1.0, 1.0, MouseButton.PRIMARY)
                bufferedImageView.fireMouseReleased(1.0, 1.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Don't highlight plane on miss click`(robot: FxRobot) {
        layerSettings.useCommonColor = false
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider, 0) {
            robot.interact {
                bufferedImageView.fireMousePressed(2.0, 2.0, MouseButton.PRIMARY)
                bufferedImageView.fireMouseReleased(2.0, 2.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Don't highlight plane on right mouse button`(robot: FxRobot) {
        layerSettings.useCommonColor = false
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider, 0) {
            robot.interact {
                bufferedImageView.fireMousePressed(1.0, 1.0, MouseButton.SECONDARY)
                bufferedImageView.fireMouseReleased(1.0, 1.0, MouseButton.SECONDARY)
            }
        }
    }

    @Test
    fun `Unhighlight plane on click`(robot: FxRobot) {
        layerSettings.useCommonColor = true
        val animationProvider = mockk<AnimationProvider>()
        ServiceLocator.registerService(animationProvider)
        testHighlighted(animationProvider, 1) {
            robot.interact {
                bufferedImageView.fireMousePressed(1.0, 1.0, MouseButton.PRIMARY)
                bufferedImageView.fireMouseReleased(1.0, 1.0, MouseButton.PRIMARY)
            }
        }
        testUnhighlighted(animationProvider, 2) {
            robot.interact {
                bufferedImageView.fireMousePressed(1.0, 1.0, MouseButton.PRIMARY)
                bufferedImageView.fireMouseReleased(1.0, 1.0, MouseButton.PRIMARY)
            }
        }
    }

    @Test
    fun `Plane view can be garbage collected after dispose`() {
        val factory = { planeView }
        testMemoryLeak(factory) {
            it.dispose()
            planeView = PlaneView(plane, frameDrawer, eventManager, viewOrder, scale.value)
        }
    }

    private fun testHighlighted(
        animationProvider: AnimationProvider,
        times: Int,
        action: () -> Unit
    ) {
        every {
            animationProvider.createColorTimeline(
                allAny(),
                layerSettings.getColor(plane),
                layerSettings.getUniqueColor(plane),
                allAny()
            )
        } answers { Timeline() }

        action()

        verify(exactly = times) {
            animationProvider.createColorTimeline(
                allAny(),
                layerSettings.getColor(plane),
                layerSettings.getUniqueColor(plane),
                allAny()
            )
        }
    }

    private fun testUnhighlighted(
        animationProvider: AnimationProvider,
        times: Int,
        action: () -> Unit
    ) {
        every {
            animationProvider.createColorTimeline(
                allAny(),
                layerSettings.getUniqueColor(plane),
                layerSettings.getColor(plane),
                allAny()
            )
        } answers { Timeline() }

        action()

        verify(exactly = times) {
            animationProvider.createColorTimeline(
                allAny(),
                layerSettings.getUniqueColor(plane),
                layerSettings.getColor(plane),
                allAny()
            )
        }
    }

    private fun assertColor(color: Color, points: List<Point> = this.points) {
        val imagePixelReader = bufferedImageView.image.pixelReader
        points.forEach { point ->
            assertEquals(
                color,
                imagePixelReader.getColor(point.x.toInt(), point.y.toInt())
            )
        }
    }
}
