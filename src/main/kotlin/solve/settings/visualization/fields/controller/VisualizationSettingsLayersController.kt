package solve.settings.visualization.fields.controller

import solve.scene.model.LayerSettings
import solve.settings.visualization.fields.model.VisualizationSettingsLayersModel
import tornadofx.Controller

class VisualizationSettingsLayersController : Controller() {
    val model = VisualizationSettingsLayersModel()

    fun setLayerFields(layers: List<LayerSettings>) {
        model.reinitializeLayers(layers)
    }
}
