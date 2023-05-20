package solve.settings.visualization.popover

import io.github.palexdev.materialfx.controls.MFXCheckbox
import io.github.palexdev.materialfx.controls.MFXSlider
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.value.ChangeListener
import javafx.scene.control.ColorPicker
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings
import solve.styles.Style
import tornadofx.*

const val ElementWidth = 270.0
fun buildLandmarkColorPicker(
    layerSettings: LayerSettings,
    sceneController: SceneController
): ColorPicker {
    val colorPicker = ColorPicker()

    colorPicker.value = layerSettings.commonColor
    colorPicker.prefWidth = ElementWidth
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
): MFXCheckbox {
    val checkBox = MFXCheckbox()
    checkBox.style {
        fontFamily = Style.FontCondensed
        fontSize = 20.0.px
    }

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
): MFXSlider {
    val slider = MFXSlider()

    if (initialSizeValue !in minValue..maxValue) {
        throw IllegalArgumentException("The initial selected size value is out of selection range!")
    }
    slider.min = minValue
    slider.max = maxValue
    slider.value = initialSizeValue
    slider.prefWidth = ElementWidth

    slider.valueProperty().addListener(changeListener)

    return slider
}
