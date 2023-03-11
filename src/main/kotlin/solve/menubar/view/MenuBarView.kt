package solve.menubar.view

import solve.DarkTheme
import solve.importer.controller.ImporterController
import solve.importer.view.ImporterView
import tornadofx.*

class MenuBarView : View() {
    val importer = find<ImporterView>()
    val controller: ImporterController by inject()

    override val root = hbox {
        addClass(DarkTheme.backgroundElement)
        button("Import project") {
            action {
                controller.directoryPath.set(null)
                controller.projectAfterPartialParsing.set(null)
                importer.openModal()
            }
        }
        button("Manage plugins")
        button("Settings")
        button("Help")
        children.forEach { it.addClass(DarkTheme.menuBarButton) }

    }
}