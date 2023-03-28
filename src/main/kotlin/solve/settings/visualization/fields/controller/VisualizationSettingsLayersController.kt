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

    private fun initializeSceneLayersIndices(
        layers: List<LayerSettings>
    ) {
        var firstPlaneLayerSettingsIndex = layers.indexOfFirst { it is LayerSettings.PlaneLayerSettings }
        if (firstPlaneLayerSettingsIndex == -1) {
            firstPlaneLayerSettingsIndex = layers.count()
        }
        val planeLayers = layers.subList(firstPlaneLayerSettingsIndex, layers.count())
        val notPlaneLayers = layers.subList(0, firstPlaneLayerSettingsIndex)

        notPlaneLayers.reversed().forEachIndexed { index, notPlane ->
            sceneController.scene.changeLayerIndex(notPlane, index)
        }
        planeLayers.reversed().forEachIndexed { index, plane ->
            sceneController.scene.changeLayerIndex(plane, index)
        }
    }
}
