package solve.settings.visualization.fields.view

import javafx.util.Callback
import solve.scene.controller.SceneController
import solve.settings.visualization.fields.controller.VisualizationSettingsLayersController
import tornadofx.*

class VisualizationSettingsLayersView: View() {
    private val sceneController: SceneController by inject()

    private val controller: VisualizationSettingsLayersController by inject()

    private val fieldsListView = listview(controller.model.layers) {
        cellFactory = Callback {
            VisualizationSettingsLayerCell(this, sceneController)
        }
    }

    override val root = fieldsListView
}
