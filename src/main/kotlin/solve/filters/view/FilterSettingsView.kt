package solve.filters.view

import javafx.scene.Node
import solve.utils.materialfx.mfxCheckbox
import tornadofx.*

class FilterSettingsView : View() {
    private val filterSettingsContentNode = borderpane {
        top = hbox {
            buildFilterSettingField("Time period", pane())
        }
    }
    override val root = filterSettingsContentNode

    private fun buildFilterSettingField(name: String, settingNode: Node) = hbox {
        mfxCheckbox()
        label(name)
        add(settingNode)
    }
}
