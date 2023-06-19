package solve.settings.visualization.popover

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.WeakChangeListener
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.scene.model.LayerSettings.LineLayerSettings.Companion.MaxWidthValue
import solve.scene.model.LayerSettings.LineLayerSettings.Companion.MinWidthValue
import solve.utils.structures.Alignment
import tornadofx.*

class LineLayerSettingsPopOverNode(
    private val lineLayerSettings: LayerSettings.LineLayerSettings,
    private val sceneController: SceneController,
    private val title: String,
    private val dialogIsClosing: SimpleBooleanProperty
) : LayerSettingsPopOverNode() {
    companion object {
        const val LayerSettingsNodePrefWidth = 260.0
        const val LayerSettingsNodePrefHeight = 90.0
    }

    private val widthSliderValueChangedEventHandler = ChangeListener<Number> { _, _, widthValue ->
        lineLayerSettings.selectedWidth = widthValue as Double
    }
    private val weakWidthSliderValueChangedEventHandler = WeakChangeListener(widthSliderValueChangedEventHandler)

    override fun getPopOverNode(): SettingsDialogNode {
        popOver.setPrefSize(LayerSettingsNodePrefWidth, LayerSettingsNodePrefHeight)

        addTitle(title)
        addSettingField("Color", buildLandmarkColorPicker(lineLayerSettings, sceneController), isLabelOnLeft = true)
        addSettingField(
            "Width",
            buildSizeSlider(
                lineLayerSettings.selectedWidth,
                MinWidthValue,
                MaxWidthValue,
                weakWidthSliderValueChangedEventHandler
            ),
            isLabelOnLeft = true
        )
        addSettingField(
            "",
            buildLandmarkUseOneColorCheckBox(lineLayerSettings),
            Alignment.Left,
            isLabelOnLeft = false
        )
        addCancel(dialogIsClosing)

        return popOver
    }
}
