package solve.importer.view

import javafx.geometry.Insets
import javafx.scene.control.Button
import solve.importer.ProjectParser.createAlert
import solve.importer.controller.ImporterController
import solve.main.MainController
import solve.menubar.view.MenuBarView
import tornadofx.*


class ButtonView : View() {
    private val controller: ImporterController by inject()

    private val mainController: MainController by inject()

    private val directoryPathView: DirectoryPathView by inject()

    private fun cancelAction() {
        controller.directoryPath.set(null)
        directoryPathView.project.set(null)
        MenuBarView().importer.close()
    }

    private fun importAction(button: Button) {
        val projectVal = directoryPathView.project.value
        try {
            mainController.sceneFacade.visualize(projectVal.layers, projectVal.frames)
            mainController.displayCatalogueFrames(projectVal.frames)
            directoryPathView.project.set(null)
            button.isDisable = true
            MenuBarView().importer.close()
        } catch (e: Exception) {
            val alert = createAlert("Visualization error")
            alert.show()
        }
    }

    override val root = vbox(6) {
        val listOfKind = mutableListOf<String>()
        label {
            visibleWhen { directoryPathView.path.isNotEmpty }

            directoryPathView.project.onChange {
                if (it != null){
                    it.frames.forEach { frame ->
                        frame.landmarkFiles.forEach { landmark ->
                            val algName = landmark.projectLayer.name
                            if (!listOfKind.contains(algName)){
                                listOfKind.add(landmark.projectLayer.name)
                            }
                        }
                    }
                    this.text = "Algorithms: " + listOfKind.toString().replace("[", "").replace("]", "")

                }
            }
        }

        padding = Insets(10.0, 0.0, 0.0, 0.0)

        hbox(10) {
            button("Cancel") {
                action {
                    cancelAction()
                }
                prefWidth = 180.0
            }
            button("Import") {
                prefWidth = 180.0
                isDisable = true
                directoryPathView.project.onChange {
                    if (directoryPathView.project.value != null) {
                        if (directoryPathView.project.value.frames.isNotEmpty()) {
                            isDisable = false
                        }
                    }
                }
                action {
                    importAction(this)
                }
            }
        }
    }
}