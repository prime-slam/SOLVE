package solve.importer.view

import javafx.geometry.Insets
import javafx.scene.control.Button
import solve.importer.controller.*
import solve.importer.model.ButtonModel
import solve.main.MainController
import solve.menubar.view.MenuBarView
import solve.project.model.ProjectFrame
import solve.utils.createAlert
import solve.utils.toStringWithoutBrackets
import tornadofx.*

class ControlPanel : View() {
    private val controller: ImporterController by inject()

    private val buttonController: ButtonController by inject()

    private val mainController: MainController by inject()

    private val importer = find<DirectoryPathView>()

    private val importButtonModel = ButtonModel()

    companion object {
        private const val ButtonWidth = 180.0
    }

    private val algorithmsLabel = label {
        val listOfKind = mutableListOf<String>()
        visibleWhen { controller.project.isNotNull }

        controller.project.onChange {
            it?.let {
                it.frames.forEach { frame ->
                    getListOfAlgorithms(frame.frame, listOfKind)
                }
                this.text = "Algorithms: " + listOfKind.toStringWithoutBrackets()
            }
        }
    }

    private val importButton = button("Import") {
        buttonController.changeDisable(importButtonModel)
        isDisable = importButtonModel.disabled.value
        prefWidth = ButtonWidth
        importButtonModel.disabled.onChange {
            isDisable = it!!
        }
        action {
            importAction(this)
        }
    }

    private val cancelButton = button("Cancel") {
        prefWidth = ButtonWidth
        action {
            cancelAction()
        }
    }

    override val root = vbox(6) {
        padding = Insets(10.0, 0.0, 0.0, 0.0)
        add(algorithmsLabel)
        hbox(10) {
            add(cancelButton)
            add(importButton)
        }
    }

    private fun cancelAction() {
        importer.close()
    }

    private fun importAction(button: Button) {
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

    private fun getListOfAlgorithms(frame: ProjectFrame, listOfKind: MutableList<String>) {
        frame.landmarkFiles.forEach { landmark ->
            val algName = landmark.projectLayer.name
            if (!listOfKind.contains(algName)) {
                listOfKind.add(landmark.projectLayer.name)
            }
        }
    }
}