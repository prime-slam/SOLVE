package sliv.tool.menubar.view

import sliv.tool.importer.view.ImporterView
import sliv.tool.main.MainController
import tornadofx.*

class MenuBarView : View() {
    val importer = find<ImporterView>()

    override val root = hbox {
        button("Import project") {
            action {
                importer.openModal()
            }
        }
        button("Manage plugins") {
        }
        button("Settings") {
        }
        button("Help") {
        }
    }
}