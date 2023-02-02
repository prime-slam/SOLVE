package solve.importer.view

import javafx.geometry.Insets
import solve.importer.controller.*
import solve.importer.model.ButtonModel
import solve.project.model.ProjectFrame
import solve.utils.toStringWithoutBrackets
import tornadofx.*

class ControlPanel : View() {
    private val controller: ImporterController by inject()

    private val buttonController: ButtonController by inject()

    companion object {
        private const val ButtonWidth = 180.0
    }

    private val cancelButton = ButtonModel("Cancel").apply {
        prefWidth = ButtonWidth
        action {
            buttonController.cancelAction()
        }
    }

    private val importButton = ButtonModel("Import").apply {
        buttonController.changeDisable(this)
        prefWidth = ButtonWidth
        action {
            buttonController.importAction(this)
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

    override val root = vbox(6) {
        padding = Insets(10.0, 0.0, 0.0, 0.0)
        add(algorithmsLabel)
        hbox(10) {
            add(cancelButton)
            add(importButton)
        }
    }
}