package solve.settings.visualization

import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.settings.visualization.fields.controller.VisualizationSettingsLayersController
import solve.settings.visualization.fields.view.VisualizationSettingsLayersView
import solve.styles.Style
import solve.utils.createInsetsWithValue
import tornadofx.*

class VisualizationSettingsView : View() {
    private class FramesLayerSettingsComparator : Comparator<LayerSettings> {
        override fun compare(firstLayerSettings: LayerSettings, secondLayerSettings: LayerSettings): Int {
            val layersSettings = listOf(firstLayerSettings, secondLayerSettings)
            return if (layersSettings.any { it.usesCanvas } &&
                layersSettings.any { !it.usesCanvas }
            ) {
                if (firstLayerSettings.usesCanvas) 1 else -1
            } else {
                firstLayerSettings.layerName.compareTo(secondLayerSettings.layerName)
            }
        }
    }

    private val sceneController: SceneController by inject()

    private val visualizationSettingsLayersView: VisualizationSettingsLayersView by inject()
    private val visualizationSettingsLayersController: VisualizationSettingsLayersController by inject()

    init {
        initializeLayersUpdating()
    }

    override val root = vbox {
        style {
            backgroundColor += Paint.valueOf(Style.SurfaceColor)
        }

        border = Border(
            BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
        )

        minWidth = VisualizationSettingsViewMinWidth

        add(visualizationSettingsLayersView)
        padding = createInsetsWithValue(5.0)
        vgrow = Priority.ALWAYS
    }

    private fun initializeLayersUpdating() {
        val framesLayerSettingsComparator = FramesLayerSettingsComparator()

        sceneController.sceneProperty.onChange { scene ->
            scene ?: return@onChange
            val newLayers = scene.layerSettings
            val sortedNewLayers = newLayers.sortedWith(framesLayerSettingsComparator)
            visualizationSettingsLayersController.setLayerFields(sortedNewLayers)
        }
    }

    companion object {
        private const val VisualizationSettingsViewMinWidth = 205.0
    }
}
