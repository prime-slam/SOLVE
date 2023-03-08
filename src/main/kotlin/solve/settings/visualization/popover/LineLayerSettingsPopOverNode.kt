package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.Slider
import javafx.scene.layout.VBox
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.utils.createInsetsWithValue
import tornadofx.add
import tornadofx.onChange

class LineLayerSettingsPopOverNode(
    private val lineLayerSettings: LayerSettings.LineLayerSettings,
    private val sceneController: SceneController
): LayerSettingsPopOverNode {
        companion object {
            const val LayerSettingsNodePrefWidth = 220.0
            const val LayerSettingsNodePrefHeight = 50.0

            const val LineWidthSliderMinValue = 0.1
            const val LineWidthSliderMaxValue = 5.0
        }

        private fun buildLineLandmarksColorPicker(): ColorPicker {
            val colorPicker = ColorPicker()

            colorPicker.value = lineLayerSettings.commonColor
            colorPicker.setOnAction {
                lineLayerSettings.commonColor = colorPicker.value
            }
            sceneController.scene.onChange {
                colorPicker.value = lineLayerSettings.commonColor
            }

            return colorPicker
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

        override fun getPopOverNode(): Node {
            val vbox = VBox()
            vbox.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

            vbox.add(createSettingField("Color", buildLineLandmarksColorPicker()))
            vbox.add(createSettingField("Width", buildLineWidthSlider()))

            vbox.padding = createInsetsWithValue(10.0)

            return vbox
        }
}
