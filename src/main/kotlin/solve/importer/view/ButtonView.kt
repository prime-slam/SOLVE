package solve.importer.view

import javafx.geometry.Insets
import javafx.scene.control.Button
import solve.importer.ProjectParser.createAlert
import solve.main.MainController
import solve.menubar.view.MenuBarView
import solve.project.model.ProjectFrame
import tornadofx.*


class ButtonView : View() {

    private val mainController: MainController by inject()

    private val directoryPathView: DirectoryPathView by inject()

    private fun cancelAction() {
        MenuBarView().importer.close()
    }

    private fun importAction(button: Button) {
        val projectVal = directoryPathView.project.value
        try {
            mainController.sceneFacade.visualize(projectVal.layers, projectVal.frames.map { it.frame })
            mainController.displayCatalogueFrames(projectVal.frames.map { it.frame })
            directoryPathView.project.set(null)
            button.isDisable = true
            MenuBarView().importer.close()
        } catch (e: Exception) {
            createAlert("Visualization error")
        }
    }

    private val cancelButton = button("Cancel") {
        action {
            cancelAction()
        }
        prefWidth = 180.0
    }

    private val importButton = button("Import") {
        isDisable = true
        directoryPathView.project.onChange {
            isDisable = it?.hasAnyErrors ?: true
        }
        prefWidth = 180.0
        action {
            importAction(this)
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

    private val algorithmsLabel = label {
        val listOfKind = mutableListOf<String>()
        visibleWhen { directoryPathView.project.isNotNull }

        directoryPathView.project.onChange {
            if (it != null) {
                it.frames.forEach { frame ->
                    getListOfAlgorithms(frame.frame, listOfKind)
                }
                this.text = "Algorithms: " + listOfKind.toString().replace("[", "").replace("]", "")
            }
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
}