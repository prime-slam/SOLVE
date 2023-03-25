package solve.settings.visualization.popover

import javafx.scene.control.CheckBox
import javafx.scene.control.ColorPicker
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import tornadofx.*

fun buildLandmarkColorPicker(
    layerSettings: LayerSettings,
    sceneController: SceneController
): ColorPicker {
    val colorPicker = ColorPicker()

    colorPicker.value = layerSettings.commonColor
    colorPicker.setOnAction {
        layerSettings.commonColor = colorPicker.value
    }
    sceneController.sceneProperty.onChange {
        colorPicker.value = layerSettings.commonColor
    }

    return colorPicker
}

fun buildLandmarkUseOneColorCheckBox(
    layerSettings: LayerSettings,
) : CheckBox {
    val checkBox = CheckBox()

    checkBox.isSelected = layerSettings.useCommonColor
    checkBox.selectedProperty().onChange { useCommonColor ->
        layerSettings.useCommonColor = useCommonColor
    }

    return checkBox
}
