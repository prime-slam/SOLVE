package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.layout.VBox
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.utils.*
import tornadofx.*

class PointLayerSettingsPopOverNode(
    private val pointLayerSettings: LayerSettings.PointLayerSettings,
    private val sceneController: SceneController
): LayerSettingsPopOverNode() {
    companion object {
        const val LayerSettingsNodePrefWidth = 220.0
        const val LayerSettingsNodePrefHeight = 50.0
    }

    private fun buildPointLandmarksColorPicker(): ColorPicker {
        val colorPicker = ColorPicker()

        colorPicker.value = pointLayerSettings.color
        colorPicker.setOnAction {
            pointLayerSettings.color = colorPicker.value
        }
        sceneController.scene.onChange {
            colorPicker.value = pointLayerSettings.color
        }

        return colorPicker
    }

    fun getPopOverNode(): Node {
        val vbox = VBox()
        vbox.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        vbox.add(createSettingField("Color", buildPointLandmarksColorPicker()))

        vbox.padding = createInsetsWithValue(10.0)

        return vbox
    }
}
