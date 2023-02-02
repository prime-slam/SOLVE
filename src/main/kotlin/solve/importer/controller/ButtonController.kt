package solve.importer.controller

import javafx.scene.control.Button
import solve.importer.model.ButtonModel
import solve.importer.view.DirectoryPathView
import solve.main.MainController
import solve.menubar.view.MenuBarView
import solve.utils.createAlert
import tornadofx.Controller
import tornadofx.onChange

class ButtonController : Controller() {
    private val importer = find<DirectoryPathView>()

    private val mainController: MainController by inject()

    private val controller: ImporterController by inject()

    fun changeDisable(model: ButtonModel) {
        model.isDisable = true
        controller.project.onChange {
            model.isDisable = it?.hasAnyErrors ?: true
        }
    }

    fun cancelAction() {
        importer.close()
    }

    fun importAction(button: Button) {
        val projectVal = controller.project.value
        try {
            mainController.visualizeProject(projectVal.layers, projectVal.frames.map { it.frame })
            mainController.displayCatalogueFrames(projectVal.frames.map { it.frame })
            controller.project.set(null)
            button.isDisable = true
            MenuBarView().importer.close()
        } catch (e: Exception) {
            createAlert("Visualization error", MenuBarView().importer.root.scene.window)
        }
    }
}