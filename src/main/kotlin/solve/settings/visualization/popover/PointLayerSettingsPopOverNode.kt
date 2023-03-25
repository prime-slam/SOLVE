package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.Slider
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.utils.structures.Alignment
import tornadofx.*

class PointLayerSettingsPopOverNode(
    private val pointLayerSettings: LayerSettings.PointLayerSettings,
    private val sceneController: SceneController
): LayerSettingsPopOverNode() {
    companion object {
        const val LayerSettingsNodePrefWidth = 260.0
        const val LayerSettingsNodePrefHeight = 90.0

        const val PointSizeSliderMinValue = 1.0
        const val PointSizeSliderMaxValue = 20.0
    }

    override fun getPopOverNode(): Node {
        popOver.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addSettingField("Color", buildLandmarkColorPicker(pointLayerSettings, sceneController))
        addSettingField("Size", buildPointSizeSlider())
        addSettingField("One color", buildLandmarkUseOneColorCheckBox(pointLayerSettings), Alignment.Left)

        return popOver
    }

    private fun buildPointSizeSlider(): Slider {
        val slider = Slider()

        val initialSelectedRadius = pointLayerSettings.selectedRadius
        if (initialSelectedRadius !in PointSizeSliderMinValue..PointSizeSliderMaxValue) {
            throw IllegalArgumentException("The initial selected radius is out of selection range!")
        }
        slider.min = PointSizeSliderMinValue
        slider.max = PointSizeSliderMaxValue
        slider.value = pointLayerSettings.selectedRadius
        slider.isShowTickLabels = true

        slider.valueProperty().onChange { radiusValue ->
            pointLayerSettings.selectedRadius = radiusValue
        }

        return slider
    }
}
