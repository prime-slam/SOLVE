package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import solve.settings.createSettingsField
import solve.utils.structures.Alignment
import tornadofx.*

abstract class LayerSettingsPopOverNode {
    companion object {
        private const val LayerSettingLabelFontSize = 16.0
        private const val LayerSettingsLabelPaddingLeft = 10.0
        private const val LayerSettingsFieldLabelWidth = 100.0
        private const val LayerSettingsSettingNodeWidth = 160.0
    }

    protected val popOver: VBox = VBox()

    fun addSettingField(name: String, settingNode: Node, settingsNodeAlignment: Alignment = Alignment.Center) {
        val fieldLabel = Label(name)
        fieldLabel.font = Font.font(LayerSettingLabelFontSize)
        fieldLabel.paddingLeft = LayerSettingsLabelPaddingLeft

        val settingsField = createSettingsField(
            fieldLabel,
            LayerSettingsFieldLabelWidth,
            settingNode,
            LayerSettingsSettingNodeWidth,
            settingsNodeAlignment)
        popOver.add(settingsField)
    }

    abstract fun getPopOverNode(): Node
}
