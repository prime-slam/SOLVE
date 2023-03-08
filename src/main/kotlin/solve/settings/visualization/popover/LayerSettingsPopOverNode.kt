package solve.settings.visualization.popover

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import solve.utils.createHGrowHBox
import tornadofx.*

interface LayerSettingsPopOverNode {
    companion object {
        private const val LayerSettingNameFontSize = 16.0
    }

    fun createSettingField(name: String, settingNode: Node): HBox {
        val hbox = HBox(3.0)

        val label = Label(name)
        label.font = Font.font(LayerSettingNameFontSize)

        hbox.add(label)
        hbox.add(createHGrowHBox())
        hbox.add(settingNode)

        return hbox
    }

    fun getPopOverNode(): Node
}
