package sliv.tool.view

import javafx.scene.Parent
import tornadofx.*

class MenuBarView : View() {
    override val root = hbox {
        button("Import project") {
        }
        button("Manage plugins") {
        }
        button("Settings") {
        }
        button("Help") {
        }
    }
}