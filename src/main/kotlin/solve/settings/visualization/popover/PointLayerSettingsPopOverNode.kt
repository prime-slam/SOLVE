package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.Slider
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

        const val PointSizeSliderMinValue = 1.0
        const val PointSizeSliderMaxValue = 20.0
    }

    private fun buildPointLandmarksColorPicker(): ColorPicker {
        val colorPicker = ColorPicker()

        colorPicker.value = pointLayerSettings.commonColor
        colorPicker.setOnAction {
            pointLayerSettings.commonColor = colorPicker.value
        }
        sceneController.scene.onChange {
            colorPicker.value = pointLayerSettings.commonColor
        }

        return colorPicker
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

    override fun getPopOverNode(): Node {
        val vbox = VBox()
        vbox.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        vbox.add(createSettingField("Color", buildPointLandmarksColorPicker()))
        vbox.add(createSettingField("Size", buildPointSizeSlider()))

        vbox.padding = createInsetsWithValue(10.0)

        return vbox
    }
}
