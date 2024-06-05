package solve.unit.rendering.engine.core.input

import junit.framework.TestCase.assertEquals
import org.joml.Vector2i
import org.junit.jupiter.api.Test
import solve.rendering.engine.core.input.MouseInputHandler.indexOfClickedLineLandmark
import solve.rendering.engine.core.input.MouseInputHandler.indexOfClickedPointLandmark
import solve.scene.model.ColorManager
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.LayerState
import solve.scene.model.Point

class MouseInputHandlerTests {
    @Test
    fun `Getting the index of the clicked line landmark (1st test)`() {
        val clickedLineIndex = indexOfClickedLineLandmark(testLineLandmarks, Vector2i(10, 3), 1f)
        assertEquals(0, clickedLineIndex)
    }

    @Test
    fun `Getting the index of the clicked line landmark (2nd test)`() {
        val clickedLineIndex = indexOfClickedLineLandmark(testLineLandmarks, Vector2i(23, 9), 1f)
        assertEquals(5, clickedLineIndex)
    }

    @Test
    fun `Getting the index of the clicked line landmark (3rd test)`() {
        val clickedLineIndex = indexOfClickedLineLandmark(testLineLandmarks, Vector2i(14, 19), 1f)
        assertEquals(4, clickedLineIndex)
    }

    @Test
    fun `Getting the index of the clicked line landmark (4th test)`() {
        val clickedLineIndex = indexOfClickedLineLandmark(testLineLandmarks, Vector2i(33, 16), 1f)
        assertEquals(6, clickedLineIndex)
    }

    @Test
    fun `Trying to get the index of the non-existing line landmark`() {
        val clickedLineIndex = indexOfClickedLineLandmark(testLineLandmarks, Vector2i(25, 26), 1f)
        assertEquals(-1, clickedLineIndex)
    }

    @Test
    fun `Getting the index of the clicked point landmark (1st test)`() {
        val clickedPointIndex = indexOfClickedPointLandmark(testPointLandmarks, Vector2i(8, 13), 1f)
        assertEquals(5, clickedPointIndex)
    }

    @Test
    fun `Getting the index of the clicked point landmark (2nd test)`() {
        val clickedPointIndex = indexOfClickedPointLandmark(testPointLandmarks, Vector2i(21, 16), 1f)
        assertEquals(8, clickedPointIndex)
    }

    @Test
    fun `Getting the index of the clicked point landmark (3rd test)`() {
        val clickedPointIndex = indexOfClickedPointLandmark(testPointLandmarks, Vector2i(26, 19), 1f)
        assertEquals(11, clickedPointIndex)
    }

    companion object {
        val testLinesLayerSettings = LayerSettings.LineLayerSettings(
            "test_lines_layer",
            "",
            ColorManager()
        )
        val testLinesLayerState = LayerState("test_lines_layer_state")
        val testLineLandmarks = listOf(
            Landmark.Line(0L, testLinesLayerSettings, testLinesLayerState, Point(4, 2), Point(31, 9)),
            Landmark.Line(1L, testLinesLayerSettings, testLinesLayerState, Point(11, 3), Point(9, 16)),
            Landmark.Line(2L, testLinesLayerSettings, testLinesLayerState, Point(5, 30), Point(31, 30)),
            Landmark.Line(3L, testLinesLayerSettings, testLinesLayerState, Point(23, 15), Point(28, 18)),
            Landmark.Line(4L, testLinesLayerSettings, testLinesLayerState, Point(8, 10), Point(19, 26)),
            Landmark.Line(5L, testLinesLayerSettings, testLinesLayerState, Point(3, 17), Point(33, 5)),
            Landmark.Line(6L, testLinesLayerSettings, testLinesLayerState, Point(33, 16), Point(30, 24))
        )

        val testPointsLayerSettings = LayerSettings.PointLayerSettings(
            "test_points_layer",
            "",
            ColorManager()
        )
        val testPointsLayerState = LayerState("test_points_layer_state")
        val testPointLandmarks = listOf(
            Landmark.Keypoint(0L, testPointsLayerSettings, testPointsLayerState, Point(6, 3)),
            Landmark.Keypoint(1L, testPointsLayerSettings, testPointsLayerState, Point(18, 5)),
            Landmark.Keypoint(2L, testPointsLayerSettings, testPointsLayerState, Point(32, 4)),
            Landmark.Keypoint(3L, testPointsLayerSettings, testPointsLayerState, Point(26, 10)),
            Landmark.Keypoint(4L, testPointsLayerSettings, testPointsLayerState, Point(32, 10)),
            Landmark.Keypoint(5L, testPointsLayerSettings, testPointsLayerState, Point(8, 13)),
            Landmark.Keypoint(6L, testPointsLayerSettings, testPointsLayerState, Point(17, 14)),
            Landmark.Keypoint(7L, testPointsLayerSettings, testPointsLayerState, Point(8, 15)),
            Landmark.Keypoint(8L, testPointsLayerSettings, testPointsLayerState, Point(20, 16)),
            Landmark.Keypoint(9L, testPointsLayerSettings, testPointsLayerState, Point(28, 19)),
            Landmark.Keypoint(10L, testPointsLayerSettings, testPointsLayerState, Point(6, 20)),
            Landmark.Keypoint(11L, testPointsLayerSettings, testPointsLayerState, Point(26, 20)),
            Landmark.Keypoint(12L, testPointsLayerSettings, testPointsLayerState, Point(13, 26)),
            Landmark.Keypoint(13L, testPointsLayerSettings, testPointsLayerState, Point(28, 19)),
            Landmark.Keypoint(14L, testPointsLayerSettings, testPointsLayerState, Point(29, 31))
        )
    }
}
