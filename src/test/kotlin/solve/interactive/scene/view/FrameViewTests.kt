package solve.interactive.scene.view

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.shape.Ellipse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import solve.interactive.InteractiveTestClass
import solve.scene.model.ColorManager
import solve.scene.model.Landmark
import solve.scene.model.Layer
import solve.scene.model.LayerSettings
import solve.scene.model.LayerState
import solve.scene.model.OrderManager
import solve.scene.model.Point
import solve.scene.model.VisualizationFrame
import solve.scene.view.FrameView
import solve.scene.view.FrameViewData
import solve.scene.view.FrameViewParameters
import solve.scene.view.association.AssociationsManager
import solve.scene.view.association.OutOfFramesLayer
import solve.testMemoryLeak
import solve.utils.Storage
import solve.utils.structures.Size

@ExtendWith(ApplicationExtension::class)
internal class FrameViewTests : InteractiveTestClass() {
    private class TestStorage : Storage<FrameView> {
        val storedElements = mutableListOf<FrameView>()

        override fun store(element: FrameView) {
            storedElements.add(element)
        }
    }

    private class TestOrderManager(private val map: Map<LayerSettings, Int>) : OrderManager<LayerSettings> {
        var orderChangedListenersCount = 0

        override fun addOrderChangedListener(action: () -> Unit) {
            orderChangedListenersCount++
        }

        override fun removeOrderChangedListener(action: () -> Unit) {}

        override fun indexOf(element: LayerSettings): Int {
            return map[element]!!
        }
    }

    private val size = Size(200.0, 100.0)
    private lateinit var frameView: FrameView
    private lateinit var scale: DoubleProperty
    private lateinit var storage: TestStorage
    private lateinit var orderManager: TestOrderManager
    private lateinit var associationsManager: AssociationsManager<VisualizationFrame, Landmark.Keypoint>
    private lateinit var image: Image
    private val layerName = "keypoints"
    private lateinit var settings: LayerSettings.PointLayerSettings
    private val points = listOf(Point(1, 1), Point(2, 5))
    private lateinit var keypoints: List<Landmark.Keypoint>
    private lateinit var frame: VisualizationFrame
    private lateinit var parameters: FrameViewParameters

    @BeforeEach
    fun setUp(robot: FxRobot) {
        storage = TestStorage()
        scale = SimpleDoubleProperty(1.0)
        settings = LayerSettings.PointLayerSettings(layerName, layerName, ColorManager())
        orderManager = TestOrderManager(mapOf(settings to 0))
        associationsManager =
            AssociationsManager(size, 10.0, scale, listOf(), SimpleIntegerProperty(10), OutOfFramesLayer())
        val scope = CoroutineScope(Dispatchers.JavaFx)
        parameters = FrameViewParameters(scope, associationsManager, orderManager)
        image = WritableImage(size.width.toInt(), size.height.toInt())
        val state = LayerState(layerName)
        keypoints = points.mapIndexed { index, point ->
            Landmark.Keypoint(index.toLong(), settings, state, point)
        }
        val layer = Layer.PointLayer(layerName, settings) { keypoints }
        frame = VisualizationFrame(1, { image }, listOf(layer))
        robot.interact {
            frameView = FrameView(size, scale, storage, 0, parameters, frame)
        }
    }

    @Test
    fun `Correct keypoints positions`() {
        val positions = frameView.children.filterIsInstance<Ellipse>()
            .map { Point(it.centerX.toInt().toShort(), it.centerY.toInt().toShort()) }
        assertEquals(points.toSet(), positions.toSet())
    }

    @Test
    fun `Scale changed`() {
        scale.value *= 2
        val positions = frameView.children.filterIsInstance<Ellipse>()
            .map { Point(it.centerX.toInt().toShort(), it.centerY.toInt().toShort()) }
        assertEquals(
            points.map {
                Point(
                    (it.x * scale.value).toInt().toShort(),
                    (it.y * scale.value).toInt().toShort()
                )
            }.toSet(),
            positions.toSet()
        )
    }

    @Test
    fun `Set null frame`(robot: FxRobot) {
        robot.interact {
            frameView.setFrame(null)
        }
        assertEquals(1, frameView.children.size)
    }

    @Test
    fun `Can be garbage collected after dispose`(robot: FxRobot) {
        robot.interact {
            val factory = { frameView }
            testMemoryLeak(factory) {
                it.dispose()
                storage.storedElements.clear()
                frameView = FrameView(size, scale, storage, 0, parameters, frame)
            }
        }
    }

    @Test
    fun `Stores on dispose`() {
        frameView.dispose()
        assertEquals(setOf(frameView), storage.storedElements.toSet())
    }

    @Test
    fun `Update frame`(robot: FxRobot) {
        val points = listOf(Point(4, 6))
        keypoints = points.mapIndexed { index, point ->
            Landmark.Keypoint(index.toLong(), settings, LayerState(layerName), point)
        }
        val layer = Layer.PointLayer(layerName, settings) { keypoints }
        frame = VisualizationFrame(1, { image }, listOf(layer))
        robot.interact {
            frameView.setFrame(frame)
        }
        val positions = frameView.children.filterIsInstance<Ellipse>()
            .map { Point(it.centerX.toInt().toShort(), it.centerY.toInt().toShort()) }
        assertEquals(points.toSet(), positions.toSet())
    }

    @Test
    fun `Reuse frame`() {
        val scope = CoroutineScope(Dispatchers.JavaFx)
        parameters = FrameViewParameters(scope, associationsManager, orderManager)
        frameView.init(FrameViewData(frame, parameters))
        assertEquals(2, orderManager.orderChangedListenersCount)
    }

    @Test
    fun `Show few layers`(robot: FxRobot) {
        val points = listOf(Point(4, 6))
        val keypoints = points.mapIndexed { index, point ->
            Landmark.Keypoint(index.toLong(), settings, LayerState(layerName), point)
        }
        val secondLayerName = "layer2"
        val settings = LayerSettings.PointLayerSettings(secondLayerName, secondLayerName, ColorManager())
        val layers = listOf(
            Layer.PointLayer(secondLayerName, settings) { keypoints },
            Layer.PointLayer(layerName, this.settings) { this.keypoints }
        )
        frame = VisualizationFrame(1, { image }, layers)
        val scope = CoroutineScope(Dispatchers.JavaFx)
        parameters = FrameViewParameters(
            scope,
            associationsManager,
            TestOrderManager(mapOf(this.settings to 0, settings to 1))
        )
        robot.interact {
            frameView.init(FrameViewData(frame, parameters))
        }
        val positions = frameView.children.filterIsInstance<Ellipse>()
            .map { Point(it.centerX.toInt().toShort(), it.centerY.toInt().toShort()) }
        assertEquals((points + this.points).toSet(), positions.toSet())
    }
}
