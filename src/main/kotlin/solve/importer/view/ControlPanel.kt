package solve.importer.view

import io.github.palexdev.materialfx.controls.MFXButton
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solve.constants.IconsImporterCheckCirclePath
import solve.constants.IconsImporterWarningPath
import solve.importer.FullParserForImport.fullParseDirectory
import solve.importer.controller.ButtonController
import solve.importer.controller.ImporterController
import solve.importer.model.ButtonModel
import solve.importer.model.FrameAfterPartialParsing
import solve.main.MainController
import solve.main.MainView
import solve.styles.Style
import solve.styles.Style.ControlButtonsSpacing
import solve.utils.createAlertForError
import solve.utils.loadResourcesImage
import solve.utils.materialfx.ControlButtonWidth
import solve.utils.materialfx.MaterialFXDialog
import solve.utils.materialfx.controlButton
import solve.utils.toStringWithoutBrackets
import tornadofx.*

class ControlPanel : View() {
    private val controller: ImporterController by inject()

    private val buttonController: ButtonController by inject()

    private val mainController: MainController by inject()

    private val importer: DirectoryPathView by inject()

    private val mainView: MainView by inject()

    private val loading: LoadingScreen by inject()

    private val importButtonModel = ButtonModel()

    private val filesCountIcon = loadResourcesImage(IconsImporterCheckCirclePath)
    private val errorsCountIcon = loadResourcesImage(IconsImporterWarningPath)

    var coroutineScope = CoroutineScope(Dispatchers.Main)

    private val algorithmsLabel = label {
        val listOfKind = mutableListOf<String>()
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }
        style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.MainFontSize};"

        controller.projectAfterPartialParsing.onChange {
            it?.let {
                it.projectFrames.forEach { frame ->
                    getListOfAlgorithms(frame, listOfKind)
                }
                this.text = "Algorithms: " + listOfKind.toStringWithoutBrackets()

                tooltip(listOfKind.toStringWithoutBrackets())
            }
        }
    }

    private val importButton = controlButton("IMPORT") {
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }
        buttonController.changeDisable(importButtonModel)
        isDisable = importButtonModel.disabled.value
        isFocusTraversable = false
        prefWidth = ControlButtonWidth

        importButtonModel.disabled.onChange { disableValue ->
            disableValue?.also { isDisable = it }
        }

        action {
            importAction(this)
        }
    }

    private val cancelButton = controlButton("CANCEL") {
        isFocusTraversable = false

        action {
            cancelAction()
        }
    }

    init {
        accelerators[KeyCodeCombination(KeyCode.ENTER)] = handler@{
            if (controller.projectAfterPartialParsing.value == null) {
                return@handler
            }
            importAction(importButton)
        }
        accelerators[KeyCodeCombination(KeyCode.ESCAPE)] = {
            cancelAction()
        }
    }

    private val countImagesLabel = label {
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }

        style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.MainFontSize};"
        controller.projectAfterPartialParsing.onChange {
            val countFiles = it?.projectFrames?.count()
            this.text = "$countFiles images found"
            this.graphic = ImageView(filesCountIcon)
        }
    }

    private val countErrorsLabel = label {
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }

        style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.MainFontSize};"
        controller.projectAfterPartialParsing.onChange {
            var countErrors = 0
            it?.projectFrames?.forEach { frame ->
                if (frame.image.errors.isNotEmpty()) {
                    countErrors += 1
                }
            }
            this.text = "$countErrors errors"
            this.graphic = ImageView(errorsCountIcon)
        }
    }

    override val root = borderpane {
        padding = Insets(0.0, 24.0, 14.0, 24.0)
        top {
            vbox(10) {
                separator().apply { visibleWhen { controller.projectAfterPartialParsing.isNotNull } }
                hbox(10) {
                    add(countImagesLabel)
                    add(countErrorsLabel)
                }
                add(algorithmsLabel)
            }
        }
        right {
            hbox(ControlButtonsSpacing) {
                add(cancelButton)
                controller.projectAfterPartialParsing.onChange {
                    this.clear()
                    if (it != null) {
                        add(cancelButton)
                        add(importButton)
                    } else {
                        add(cancelButton)
                    }
                }
            }
        }
    }

    private fun cancelAction() {
        controller.directoryPath.set(null)
        importer.close()
    }

    private fun showLoading() {
        MaterialFXDialog.changeContent(mainView.content, loading.root)
    }

    private fun importAction(button: MFXButton) {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            showLoading()
            val projectVal =
                withContext(Dispatchers.IO) { fullParseDirectory(controller.projectAfterPartialParsing.value) }
            try {
                mainController.visualizeProject(projectVal.layers, projectVal.frames)
                mainController.displayCatalogueFrames(projectVal.frames)
                button.isDisable = true

                mainView.dialog.close()
            } catch (e: Exception) {
                createAlertForError("Visualization error")
            }
        }
    }

    private fun getListOfAlgorithms(frame: FrameAfterPartialParsing, listOfKind: MutableList<String>) {
        listOfKind.clear()
        frame.outputs.forEach { output ->
            val algName = output.algorithmName
            if (!listOfKind.contains(algName)) {
                listOfKind.add(algName)
            }
        }
    }
}
