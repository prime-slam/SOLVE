package solve.settings.visualization.popover

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.WeakChangeListener
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.scene.model.LayerSettings.PointLayerSettings.Companion.MaxSizeValue
import solve.scene.model.LayerSettings.PointLayerSettings.Companion.MinSizeValue
import solve.utils.structures.Alignment
import tornadofx.*

class PointLayerSettingsPopOverNode(
    private val pointLayerSettings: LayerSettings.PointLayerSettings,
    private val sceneController: SceneController,
    private val title: String,
    private val dialogIsClosing: SimpleBooleanProperty
) : LayerSettingsPopOverNode() {
    private val radiusSliderValueChangedEventHandler = ChangeListener<Number> { _, _, radiusValue ->
        pointLayerSettings.selectedRadius = radiusValue as Double
    }
    private val weakRadiusSliderValueChangedEventHandler = WeakChangeListener(radiusSliderValueChangedEventHandler)

    override fun getPopOverNode(): SettingsDialogNode {
        popOver.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addTitle(title)
        addSettingField("Color", buildLandmarkColorPicker(pointLayerSettings, sceneController), isLabelOnLeft = true)
        addSettingField(
            "Size",
            buildSizeSlider(
                pointLayerSettings.selectedRadius,
                MinSizeValue,
                MaxSizeValue,
                weakRadiusSliderValueChangedEventHandler
            ),
            isLabelOnLeft = true
        )
        addSettingField(
            "",
            buildLandmarkUseOneColorCheckBox(pointLayerSettings),
            Alignment.Left,
            isLabelOnLeft = false
        )
        addCancel(dialogIsClosing)

        return popOver
    }

    companion object {
        const val LayerSettingsNodePrefWidth = 260.0
        const val LayerSettingsNodePrefHeight = 90.0
    }
}
