package solve.unit.scene.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import solve.scene.model.*

internal class LayerSettingsTests {
    @Test
    fun `Provides unique color if use common color is false`() {
        val layerSettings = LayerSettings.PointLayerSettings("layer", "layer", ColorManager())
        layerSettings.useCommonColor = false
        val layerState = LayerState("layer")
        val landmark1 = Landmark.Keypoint(1, layerSettings, layerState, Point(0, 0))
        val landmark2 = Landmark.Keypoint(2, layerSettings, layerState, Point(0, 0))

        assertNotEquals(layerSettings.getColor(landmark1), layerSettings.getColor(landmark2))
    }

    @Test
    fun `Provides common color if use common color is true`() {
        val layerSettings = LayerSettings.PointLayerSettings("layer", "layer", ColorManager())
        layerSettings.useCommonColor = true
        val layerState = LayerState("layer")
        val landmark1 = Landmark.Keypoint(1, layerSettings, layerState, Point(0, 0))
        val landmark2 = Landmark.Keypoint(2, layerSettings, layerState, Point(0, 0))

        assertEquals(layerSettings.getColor(landmark1), layerSettings.getColor(landmark2))
    }

    @Test
    fun `Provides unique color on demand`() {
        val layerSettings = LayerSettings.PointLayerSettings("layer", "layer", ColorManager())
        layerSettings.useCommonColor = true
        val layerState = LayerState("layer")
        val landmark1 = Landmark.Keypoint(1, layerSettings, layerState, Point(0, 0))
        val landmark2 = Landmark.Keypoint(2, layerSettings, layerState, Point(0, 0))

        assertNotEquals(layerSettings.getUniqueColor(landmark1), layerSettings.getColor(landmark2))
    }

    @Test
    fun `Provides color with opacity`() {
        val layerSettings = LayerSettings.PointLayerSettings("layer", "layer", ColorManager())
        layerSettings.useCommonColor = false
        val layerState = LayerState("layer")
        val landmark = Landmark.Keypoint(1, layerSettings, layerState, Point(0, 0))
        val color = layerSettings.getColor(landmark)
        val opacity = 0.342
        layerSettings.opacity = opacity
        val colorWithOpacity = layerSettings.getColorWithOpacity(landmark)

        assertEquals(color.red, colorWithOpacity.red)
        assertEquals(color.green, colorWithOpacity.green)
        assertEquals(color.blue, colorWithOpacity.blue)
        assertEquals(opacity, colorWithOpacity.opacity, 0.001)
    }
}