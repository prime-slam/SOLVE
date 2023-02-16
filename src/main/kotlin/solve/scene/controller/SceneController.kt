package solve.scene.controller

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import solve.scene.model.*
import tornadofx.*

class SceneController : Controller() {
    val scene = SimpleObjectProperty(Scene(emptyList(), emptyList()))

    fun getLayersSettingsWithType(landmarkType: LandmarkType) = when (landmarkType) {
        LandmarkType.Keypoint -> scene.value.layerSettings.filterIsInstance<LayerSettings.PointLayerSettings>()
        LandmarkType.Line -> scene.value.layerSettings.filterIsInstance<LayerSettings.LineLayerSettings>()
        LandmarkType.Plane -> scene.value.layerSettings.filterIsInstance<LayerSettings.PlaneLayerSettings>()
    }
}
