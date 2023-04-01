package solve.menubar.view

import javafx.stage.StageStyle
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
                controller.projectAfterPartialParsing.set(null)
                importer.openModal(StageStyle.UNDECORATED)
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
