package solve.importer.view

import javafx.geometry.Insets
import javafx.scene.control.Button
import kotlinx.coroutines.*
import solve.importer.controller.*
import solve.importer.model.ButtonModel
import solve.importer.model.FrameAfterPartialParsing
import solve.main.MainController
import solve.menubar.view.MenuBarView
import solve.utils.createAlertForError
import solve.utils.toStringWithoutBrackets
import tornadofx.*
import solve.importer.FullParserForImport.fullParseDirectory
import solve.project.model.Project

class ControlPanel : View() {
    private val controller: ImporterController by inject()

    private val buttonController: ButtonController by inject()

    private val mainController: MainController by inject()

    private val importer = find<DirectoryPathView>()

    private val importButtonModel = ButtonModel()

    var coroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val ButtonWidth = 180.0
    }

    private val algorithmsLabel = label {

        val listOfKind = mutableListOf<String>()
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }

        controller.projectAfterPartialParsing.onChange {
            it?.let {
                it.projectFrames.forEach { frame ->
                    getListOfAlgorithms(frame, listOfKind)
                }
                this.text = "Algorithms: " + listOfKind.toStringWithoutBrackets()
            }
        }
    }

    private val importButton = button("Import") {
        buttonController.changeDisable(importButtonModel)
        isDisable = importButtonModel.disabled.value
        prefWidth = ButtonWidth
        importButtonModel.disabled.onChange { disableValue ->
            disableValue?.also { isDisable = it }
        }

        action {
            coroutineScope = CoroutineScope(Dispatchers.Main)

            coroutineScope.launch {
                showLoading()
                val projectVal =
                    withContext(Dispatchers.IO) { fullParseDirectory(controller.projectAfterPartialParsing.value) }

                importAction(this@button, projectVal)
            }
        }
    }

    private val cancelButton = button("Cancel") {
        prefWidth = ButtonWidth
        action {
            cancelAction()
        }
    }

    override val root = vbox(6) {
        padding = Insets(8.0, 10.0, 8.0, 10.0)
//        padding = Insets(10.0, 0.0, 0.0, 0.0)
        separator { visibleWhen { controller.projectAfterPartialParsing.isNotNull } }
        add(algorithmsLabel)
        hbox(10) {
            add(cancelButton)
            add(importButton)
        }
    }

    private fun cancelAction() {
        importer.close()
    }

    private fun showLoading() {
        val loadingScreen = LoadingScreen()

        find(ImporterView::class).replaceWith(loadingScreen)
    }

    private fun importAction(button: Button, projectVal: Project) {
        try {
            mainController.visualizeProject(projectVal.layers, projectVal.frames)
            mainController.displayCatalogueFrames(projectVal.frames)
            controller.projectAfterPartialParsing.set(null)
            button.isDisable = true
            MenuBarView().importer.close()
        } catch (e: Exception) {
            createAlertForError("Visualization error", find<ImporterView>().root.scene.window)
        }
    }

    private fun getListOfAlgorithms(frame: FrameAfterPartialParsing, listOfKind: MutableList<String>) {
        frame.outputs.forEach { output ->
            val algName = output.algorithmName.split("_").first()
            if (!listOfKind.contains(algName)) {
                listOfKind.add(algName)
            }
        }
    }
}