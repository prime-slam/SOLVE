package solve.settings.visualization.dialog

import javafx.scene.Node
import javafx.scene.text.Font
import solve.scene.controller.SceneController
import solve.utils.createHGrowHBox
import tornadofx.*

abstract class LayerSettingsDialogNode: View() {
    protected val LayerSettingNameFontSize = 16.0

    protected val LayerSettingsWindowWidth = 250.0
    protected val LayerSettingsWindowHeight = 50.0

    protected val LayerSettingsWindowMinWidth = 250.0
    protected val LayerSettingsWindowMinHeight = 50.0

    protected val sceneController: SceneController by inject()

    protected fun createSettingField(name: String, settingNode: Node) = hbox(3) {
        label(name) {
            font = Font.font(LayerSettingNameFontSize)
        }
        add(createHGrowHBox())
        add(settingNode)
    }
}
