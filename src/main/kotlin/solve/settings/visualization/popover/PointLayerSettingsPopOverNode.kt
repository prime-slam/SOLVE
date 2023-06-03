package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.Slider
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.scene.model.LayerSettings.PointLayerSettings.Companion.MaxSizeValue
import solve.scene.model.LayerSettings.PointLayerSettings.Companion.MinSizeValue
import solve.utils.structures.Alignment
import tornadofx.*

class PointLayerSettingsPopOverNode(
    private val pointLayerSettings: LayerSettings.PointLayerSettings,
    private val sceneController: SceneController
) : LayerSettingsPopOverNode() {
    private val radiusSliderValueChangedEventHandler = ChangeListener<Number> { _, _, radiusValue ->
        pointLayerSettings.selectedRadius = radiusValue as Double
    }
    private lateinit var radiusSlider: Slider

    override fun getPopOver(): Node {
        popOverNode.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addSettingField("Color", buildLandmarkColorPicker(pointLayerSettings, sceneController))

        radiusSlider = buildSizeSlider(
            pointLayerSettings.selectedRadius,
            MinSizeValue,
            MaxSizeValue,
            radiusSliderValueChangedEventHandler
        )
        addSettingField(
            "Size",
            radiusSlider
        )

        addSettingField("One color", buildLandmarkUseOneColorCheckBox(pointLayerSettings), Alignment.Left)

        return popOverNode
    }

    override fun removeBindings() {
        radiusSlider.valueProperty().removeListener(radiusSliderValueChangedEventHandler)
    }

    companion object {
        const val LayerSettingsNodePrefWidth = 260.0
        const val LayerSettingsNodePrefHeight = 90.0
    }
}
