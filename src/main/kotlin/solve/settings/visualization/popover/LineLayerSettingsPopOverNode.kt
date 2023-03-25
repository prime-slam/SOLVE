package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.Slider
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.utils.structures.Alignment
import tornadofx.*

class LineLayerSettingsPopOverNode(
    private val lineLayerSettings: LayerSettings.LineLayerSettings,
    private val sceneController: SceneController
): LayerSettingsPopOverNode() {
    companion object {
        const val LayerSettingsNodePrefWidth = 260.0
        const val LayerSettingsNodePrefHeight = 90.0

        const val LineWidthSliderMinValue = 1.0
        const val LineWidthSliderMaxValue = 10.0
    }

    override fun getPopOverNode(): Node {
        popOver.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addSettingField("Color", buildLandmarkColorPicker(lineLayerSettings, sceneController))
        addSettingField("Width", buildLineWidthSlider())
        addSettingField("One color", buildLandmarkUseOneColorCheckBox(lineLayerSettings), Alignment.Left)

        return popOver
    }

    private fun buildLineWidthSlider(): Slider {
        val slider = Slider()

        val initialSelectedWidth = lineLayerSettings.selectedWidth
        if (initialSelectedWidth !in LineWidthSliderMinValue..LineWidthSliderMaxValue) {
            throw IllegalArgumentException("The initial selected line width is out of selection range!")
        }
        slider.min = LineWidthSliderMinValue
        slider.max = LineWidthSliderMaxValue
        slider.value = lineLayerSettings.selectedWidth
        slider.isShowTickLabels = true

        slider.valueProperty().onChange { widthValue ->
            lineLayerSettings.selectedWidth = widthValue
        }

        return slider
    }
}
