package sliv.tool.menubar.view

import sliv.tool.importer.view.ImporterView

import tornadofx.*

class MenuBarView : View() {

    override val root = hbox {
        button("Import project") {
            action{
                ImporterView().openModal()
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