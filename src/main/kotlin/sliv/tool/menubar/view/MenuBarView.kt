package sliv.tool.menubar.view

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