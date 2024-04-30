package solve.settings.visualization.fields.controller

import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.settings.visualization.fields.model.VisualizationSettingsLayersModel
import tornadofx.Controller

class VisualizationSettingsLayersController : Controller() {
    val model = VisualizationSettingsLayersModel()

    private val sceneController: SceneController by inject()

    fun setLayerFields(layers: List<LayerSettings>) {
        model.reinitializeLayers(layers)
        initializeSceneLayersIndices(layers)
    }

    private fun initializeSceneLayersIndices(layers: List<LayerSettings>) {
        val planeLayers = layers.filter { it.usesCanvas }
        val notPlaneLayers = layers.filterNot { !it.usesCanvas }

        notPlaneLayers.asReversed().forEachIndexed { index, notPlane ->
            sceneController.scene.changeLayerIndex(notPlane, index)
        }
        planeLayers.asReversed().forEachIndexed { index, plane ->
            sceneController.scene.changeLayerIndex(plane, index)
        }
    }
}
