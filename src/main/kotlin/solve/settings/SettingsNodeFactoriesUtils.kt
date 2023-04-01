package solve.settings

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import solve.utils.createHGrowHBox
import solve.utils.structures.Alignment
import tornadofx.add

fun createSettingsField(
    fieldLabel: Label,
    fieldLabelWidth: Double,
    settingNode: Node,
    settingNodeWidth: Double,
    settingsNodeAlignment: Alignment = Alignment.Center,
    fieldHeight: Double = 35.0
) : HBox {
    val fieldHBox = HBox()
    val settingsNodeHBox = HBox()

    val labelHBox = HBox()
    labelHBox.add(fieldLabel)
    labelHBox.alignment = Pos.CENTER
    fieldHBox.add(fieldLabel)

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

    fieldHBox.add(createHGrowHBox())
    fieldHBox.add(settingsNodeHBox)

    return fieldHBox
}
