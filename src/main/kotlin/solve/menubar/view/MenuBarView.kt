package solve.menubar.view

import solve.importer.controller.ImporterController
import solve.importer.view.ImporterView
import tornadofx.*

class MenuBarView : View() {
    val importer = find<ImporterView>()
    val controller: ImporterController by inject()

    override val root = hbox {
        button("Import project") {
            action {
                controller.directoryPath.set(null)
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