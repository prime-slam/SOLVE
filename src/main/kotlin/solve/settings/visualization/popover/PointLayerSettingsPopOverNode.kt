package solve.settings.visualization.popover

import javafx.beans.value.WeakChangeListener
import javafx.scene.Node
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

    private val radiusSliderValueChangedEventHandler = ChangeListener<Number> { _, _, radiusValue ->
        pointLayerSettings.selectedRadius = radiusValue as Double
    }
    private val weakRadiusSliderValueChangedEventHandler = WeakChangeListener(radiusSliderValueChangedEventHandler)

    override fun getPopOverNode(): Node {
        popOver.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addSettingField("Color", buildLandmarkColorPicker(pointLayerSettings, sceneController))
        addSettingField("Size", buildSizeSlider(
            pointLayerSettings.selectedRadius,
            PointSizeSliderMinValue,
            PointSizeSliderMaxValue,
            weakRadiusSliderValueChangedEventHandler
        ))
        addSettingField("One color", buildLandmarkUseOneColorCheckBox(pointLayerSettings), Alignment.Left)

        return popOver
    }
}
