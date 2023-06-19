package solve.settings

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import solve.utils.structures.Alignment
import tornadofx.*

fun createSettingsField(
    fieldLabel: Label,
    fieldLabelWidth: Double,
    settingNode: Node,
    settingNodeWidth: Double,
    settingsNodeAlignment: Alignment = Alignment.Center,
    isLabelOnLeft: Boolean,
    fieldHeight: Double = 35.0
): HBox {
    val fieldHBox = HBox(10.0)
    val settingsNodeHBox = HBox()

    val labelHBox = HBox()
    labelHBox.add(fieldLabel)
    labelHBox.alignment = Pos.CENTER
    fieldLabel.prefWidth = fieldLabelWidth
    fieldHBox.prefHeight = fieldHeight

    val wrappedSettingNode = HBox(settingNode)
    wrappedSettingNode.alignment = when (settingsNodeAlignment) {
        Alignment.Left -> Pos.CENTER_LEFT
        Alignment.Center -> Pos.CENTER
        Alignment.Right -> Pos.CENTER_RIGHT
    }
    settingsNodeHBox.add(wrappedSettingNode)
    settingsNodeHBox.prefWidth = settingNodeWidth

    if (isLabelOnLeft) {
        fieldHBox.add(fieldLabel)
        fieldHBox.add(settingsNodeHBox)
    } else {
        wrappedSettingNode.paddingLeft = 10.0
        settingsNodeHBox.prefWidth = 5.0
        fieldHBox.add(settingsNodeHBox)
        fieldHBox.add(fieldLabel)
        fieldHBox.spacing = 0.0
    }

    return fieldHBox
}
