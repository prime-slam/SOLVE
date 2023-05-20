package solve.settings.visualization.popover

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import solve.settings.createSettingsField
import solve.styles.SettingsDialogStylesheet
import solve.styles.Style
import solve.utils.materialfx.mfxButton
import solve.utils.structures.Alignment
import tornadofx.*

abstract class LayerSettingsPopOverNode {
    protected val popOver = SettingsDialogNode().apply {
        addStylesheet(SettingsDialogStylesheet::class)
    }

    protected fun addCancel(dialogClosingController: DialogClosingController) {
        val borderpane = BorderPane().apply {
            right = mfxButton("CANCEL") {
                BorderPane.setMargin(this, Insets(0.0, 24.0, 24.0, 0.0))
                maxWidth = 75.0
                prefHeight = 23.0
                style = Style.ButtonStyle
                action {
                    dialogClosingController.isClosing.value = true
                    dialogClosingController.isClosing.value = false
                }
            }
        }
        popOver.add(borderpane)
    }


    protected fun addTitle(title: String) {
        popOver.title = title
        popOver.addTitle()

    }

    protected fun addSettingField(
        name: String,
        settingNode: Node,
        settingsNodeAlignment: Alignment = Alignment.Center,
        isLabelOnLeft: Boolean,
    ) {
        val fieldLabel = Label(name)
        fieldLabel.paddingLeft = LayerSettingsLabelPaddingLeft

        val settingsField = createSettingsField(
            fieldLabel,
            LayerSettingsFieldLabelWidth,
            settingNode,
            LayerSettingsSettingNodeWidth,
            settingsNodeAlignment,
            isLabelOnLeft
        )
        popOver.add(settingsField)
    }

    abstract fun getPopOverNode(): SettingsDialogNode

    companion object {
        private const val LayerSettingLabelFontSize = 20.0
        private const val LayerSettingsLabelPaddingLeft = 10.0
        private const val LayerSettingsFieldLabelWidth = 100.0
        private const val LayerSettingsSettingNodeWidth = 300.0
    }
}
