package solve.menubar.view

import solve.importer.view.ImporterView
import solve.main.MainController
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