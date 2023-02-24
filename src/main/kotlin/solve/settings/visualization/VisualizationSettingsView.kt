package solve.settings.visualization

import javafx.scene.layout.*
import javafx.scene.paint.Color
import solve.scene.controller.SceneController
import solve.settings.visualization.fields.controller.VisualizationSettingsLayersController
import solve.settings.visualization.fields.view.VisualizationSettingsLayersView
import solve.utils.*
import tornadofx.*

class VisualizationSettingsView: View() {
    private val sceneController: SceneController by inject()

    private val visualizationSettingsLayersView: VisualizationSettingsLayersView by inject()
    private val visualizationSettingsLayersController: VisualizationSettingsLayersController by inject()

    init {
        initializeLayersUpdating()
    }

    override val root = vbox {
        add(visualizationSettingsLayersView)
        vbox {
            vbox(5) {
                padding = createInsetsWithValue(5.0)
                usePrefSize = true
            }
            border =
                Border(BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))
            hgrow = Priority.ALWAYS
            padding = createInsetsWithValue(3.0)
        }
        padding = createInsetsWithValue(5.0)
    }

    private fun initializeLayersUpdating() {
        sceneController.scene.onChange { scene ->
            scene ?: return@onChange
            visualizationSettingsLayersController.setLayerFields(scene.layerSettings)
        }
    }
}
