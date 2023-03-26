package solve.settings.visualization

import javafx.scene.layout.*
import javafx.scene.paint.Color
import solve.scene.controller.SceneController
import solve.settings.visualization.fields.controller.VisualizationSettingsLayersController
import solve.settings.visualization.fields.view.VisualizationSettingsLayersView
import solve.utils.*
import tornadofx.*

class VisualizationSettingsView: View() {
    companion object {
        private const val VisualizationSettingsViewMinWidth = 205.0
    }

    private val sceneController: SceneController by inject()

    private val visualizationSettingsLayersView: VisualizationSettingsLayersView by inject()
    private val visualizationSettingsLayersController: VisualizationSettingsLayersController by inject()

    init {
        initializeLayersUpdating()
    }

    override val root = vbox {
        minWidth = VisualizationSettingsViewMinWidth

        add(visualizationSettingsLayersView)
        vbox {
            border =
                Border(BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))
            hgrow = Priority.ALWAYS
        }
        padding = createInsetsWithValue(5.0)
        vgrow = Priority.ALWAYS
    }

    private fun initializeLayersUpdating() {
        sceneController.sceneProperty.onChange { scene ->
            scene ?: return@onChange
            val newLayers = scene.getFramesLayerSettings()
            visualizationSettingsLayersController.setLayerFields(newLayers)
        }
    }
}
