package solve.menubar.view

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import javafx.geometry.Insets
import solve.importer.controller.ImporterController
import solve.importer.view.ImporterView
import solve.importer.view.LoadingScreen
import solve.importer.view.MaterialFXDialog
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.hbox

class MenuBarView : View() {
    val importer = find<ImporterView>()

    var content = MFXGenericDialog()
    var dialog = MFXStageDialog()


    val aaa = find<LoadingScreen>()

    val controller: ImporterController by inject()

    override val root = hbox {
        button("Import project") {
            action {

                controller.directoryPath.set(null)
                controller.projectAfterPartialParsing.set(null)
                content = MaterialFXDialog().createGenericDialog()
                dialog = MaterialFXDialog().createStageDialog(content)
                dialog.show()

                content.padding = Insets(0.0,0.0,10.0,0.0)



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
