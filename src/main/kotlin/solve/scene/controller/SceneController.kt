package solve.scene.controller

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import solve.scene.model.*
import tornadofx.*

class SceneController : Controller() {
    val scene = SimpleObjectProperty(Scene(emptyList(), emptyList()))

    fun getPointLayersColor(): Color? {
        val layerSettings = getLayerSettingsWithType(LandmarkType.Keypoint)

        if (layerSettings.isEmpty()) {
            return null
        }
        return (layerSettings.first() as LayerSettings.PointLayerSettings).color
    }

    fun setPointLayersColor(color: Color) {
        val layerSettings = getLayerSettingsWithType(LandmarkType.Keypoint)
        layerSettings.forEach { (it as LayerSettings.PointLayerSettings).color = color }
    }

    private fun getLayerSettingsWithType(landmarkType: LandmarkType) = when (landmarkType) {
        LandmarkType.Keypoint -> scene.value.layerSettings.filterIsInstance<LayerSettings.PointLayerSettings>()
        LandmarkType.Line -> scene.value.layerSettings.filterIsInstance<LayerSettings.LineLayerSettings>()
        LandmarkType.Plane -> scene.value.layerSettings.filterIsInstance<LayerSettings.PlaneLayerSettings>()
    }
}
