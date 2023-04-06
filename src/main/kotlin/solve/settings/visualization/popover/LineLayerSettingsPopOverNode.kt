package solve.settings.visualization.popover

import javafx.beans.value.WeakChangeListener
import javafx.scene.Node
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

    private val widthSliderValueChangedEventHandler = ChangeListener<Number> { _, _, widthValue ->
        lineLayerSettings.selectedWidth = widthValue as Double
    }
    private val weakWidthSliderValueChangedEventHandler = WeakChangeListener(widthSliderValueChangedEventHandler)

    override fun getPopOverNode(): Node {
        popOver.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addSettingField("Color", buildLandmarkColorPicker(lineLayerSettings, sceneController))
        addSettingField("Width", buildSizeSlider(
            lineLayerSettings.selectedWidth,
            LineWidthSliderMinValue,
            LineWidthSliderMaxValue,
            weakWidthSliderValueChangedEventHandler
        ))
        addSettingField("One color", buildLandmarkUseOneColorCheckBox(lineLayerSettings), Alignment.Left)

        return popOver
    }
}
