package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.Slider
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.scene.model.LayerSettings.LineLayerSettings.Companion.MaxWidthValue
import solve.scene.model.LayerSettings.LineLayerSettings.Companion.MinWidthValue
import solve.utils.structures.Alignment
import tornadofx.*

class LineLayerSettingsPopOverNode(
    private val lineLayerSettings: LayerSettings.LineLayerSettings,
    private val sceneController: SceneController
) : LayerSettingsPopOverNode() {
    private val widthSliderValueChangedEventHandler = ChangeListener<Number> { _, _, widthValue ->
        lineLayerSettings.selectedWidth = widthValue as Double
    }
    private lateinit var widthSlider: Slider

    override fun getPopOver(): Node {
        popOverNode.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addSettingField("Color", buildLandmarkColorPicker(lineLayerSettings, sceneController))
        widthSlider = buildSizeSlider(
            lineLayerSettings.selectedWidth,
            MinWidthValue,
            MaxWidthValue,
            widthSliderValueChangedEventHandler
        )
        addSettingField(
            "Width",
            widthSlider
        )
        addSettingField("One color", buildLandmarkUseOneColorCheckBox(lineLayerSettings), Alignment.Left)

        return popOverNode
    }

    override fun removeBindings() {
        widthSlider.valueProperty().removeListener(widthSliderValueChangedEventHandler)
    }

    companion object {
        const val LayerSettingsNodePrefWidth = 260.0
        const val LayerSettingsNodePrefHeight = 90.0
    }
}
