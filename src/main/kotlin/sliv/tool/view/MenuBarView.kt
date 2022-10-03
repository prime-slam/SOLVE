package sliv.tool.view

import javafx.scene.Parent
import tornadofx.*

class MenuBarView : View() {
    override val root = hbox {
        button("Import project") {
            action {
                ImportProjectView().openModal()
            }
        }
        button("Manage plugins") {
            action {
                PluginManagerView().openModal()
            }
        }
        button("Settings") {
            action {
                ToolSettingsView().openModal()
            }
        }
        button("Help") {
            action {
                HelpView().openModal()
            }
        }
    }
}