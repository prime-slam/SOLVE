package solve.settings.visualization.popover

import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.value.ChangeListener
import javafx.scene.control.CheckBox
import javafx.scene.control.ColorPicker
import javafx.scene.control.Slider
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings

fun buildLandmarkColorPicker(
    layerSettings: LayerSettings,
    sceneController: SceneController
): ColorPicker {
    val colorPicker = ColorPicker()

    colorPicker.value = layerSettings.commonColor
    colorPicker.setOnAction {
        layerSettings.commonColor = colorPicker.value
    }

    val sceneChangedEventHandler = InvalidationListener {
        colorPicker.value = layerSettings.commonColor
    }
    val weakSceneChangedEventHandler = WeakInvalidationListener(sceneChangedEventHandler)

    sceneController.sceneProperty.addListener(weakSceneChangedEventHandler)

    return colorPicker
}

fun buildLandmarkUseOneColorCheckBox(
    layerSettings: LayerSettings
): CheckBox {
    val checkBox = CheckBox()

    checkBox.isSelected = layerSettings.useCommonColor

    val checkBoxValueChangedEventHandler = ChangeListener { _, _, useCommonColor ->
        layerSettings.useCommonColor = useCommonColor
    }

    checkBox.selectedProperty().addListener(checkBoxValueChangedEventHandler)

    return checkBox
}

fun buildSizeSlider(
    initialSizeValue: Double,
    minValue: Double,
    maxValue: Double,
    changeListener: ChangeListener<Number>
): Slider {
    val slider = Slider()

    if (initialSizeValue !in minValue..maxValue) {
        throw IllegalArgumentException("The initial selected size value is out of selection range!")
    }
    slider.min = minValue
    slider.max = maxValue
    slider.value = initialSizeValue
    slider.isShowTickLabels = true

    slider.valueProperty().addListener(changeListener)

    return slider
}
