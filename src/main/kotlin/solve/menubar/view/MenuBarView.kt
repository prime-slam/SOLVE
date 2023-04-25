package solve.menubar.view

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import javafx.geometry.Insets
import solve.importer.controller.ImporterController
import solve.importer.view.ImporterView
import solve.main.MainView
import solve.utils.MaterialFXDialog
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.hbox

class MenuBarView : View() {
    val importer: ImporterView by inject()

    private val mainView: MainView by inject()

    var content = MFXGenericDialog()
    var dialog = MFXStageDialog()

    val controller: ImporterController by inject()

    override val root = hbox {
        button("Import project") {
            action {
                controller.directoryPath.set(null)
                controller.projectAfterPartialParsing.set(null)
                content = MaterialFXDialog.createGenericDialog(importer.root)
                dialog = MaterialFXDialog.createStageDialog(content, mainView.currentStage, mainView.root)
                dialog.show()
                content.padding = Insets(0.0, 0.0, 10.0, 0.0)
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
