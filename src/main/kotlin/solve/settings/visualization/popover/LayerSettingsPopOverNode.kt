package solve.settings.visualization.popover

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import solve.utils.createHGrowHBox
import solve.utils.structures.Alignment
import tornadofx.*

abstract class LayerSettingsPopOverNode {
    companion object {
        private const val LayerSettingNameFontSize = 16.0
        private const val LayerSettingsLabelPaddingLeft = 10.0
        private const val LayerSettingsFieldLabelPrefWidth = 100.0
    }

    protected val popOver: VBox = VBox()

    fun addSettingField(name: String, settingNode: Node, settingsNodeAlignment: Alignment = Alignment.Center) {
        val settingsField = createSettingsField(name, settingNode, settingsNodeAlignment)
        popOver.add(settingsField)
    }

    private fun createSettingsField(
        name: String,
        settingNode: Node,
        settingsNodeAlignment: Alignment = Alignment.Center
    ) : HBox {
        val fieldHBox = HBox()
        val settingsNodeHBox = HBox()

        val label = Label(name)
        label.font = Font.font(LayerSettingNameFontSize)
        label.prefWidth = LayerSettingsFieldLabelPrefWidth
        label.paddingLeft = LayerSettingsLabelPaddingLeft

        val labelHBox = HBox()
        labelHBox.add(label)
        labelHBox.alignment = Pos.CENTER_LEFT
        fieldHBox.add(label)

        when (settingsNodeAlignment) {
            Alignment.Left -> {
                settingsNodeHBox.add(settingNode)
                settingsNodeHBox.add(createHGrowHBox())
            }
            Alignment.Right -> {
                settingsNodeHBox.add(createHGrowHBox())
                settingsNodeHBox.add(settingNode)
            }
            Alignment.Center -> {
                settingsNodeHBox.add(createHGrowHBox())
                settingsNodeHBox.add(settingNode)
                settingsNodeHBox.add(createHGrowHBox())
            }
        }
        settingsNodeHBox.alignment = Pos.CENTER_RIGHT
        fieldHBox.add(settingsNodeHBox)

        return fieldHBox
    }

    abstract fun getPopOverNode(): Node
}
