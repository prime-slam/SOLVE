package solve.importer.view

import io.github.palexdev.materialfx.controls.MFXButton
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import javafx.scene.paint.Paint
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
import solve.menubar.view.MenuBarView
import solve.project.model.Project
import solve.styles.Style
import solve.utils.MaterialFXDialog
import solve.utils.createAlertForError
import solve.utils.loadResourcesImage
import solve.utils.mfxButton
import solve.utils.toStringWithoutBrackets
import tornadofx.*

class ControlPanel : View() {
    private val controller: ImporterController by inject()

    private val buttonController: ButtonController by inject()

    private val mainController: MainController by inject()

    private val buttonStyle = Style.buttonStyle

    private val importer: DirectoryPathView by inject()

    private val menuBarView: MenuBarView by inject()

    private val loading: LoadingScreen by inject()

    private val importButtonModel = ButtonModel()

    private val filesCountIcon = loadResourcesImage(IconsImporterCheckCirclePath)
    private val errorsCountIcon = loadResourcesImage(IconsImporterWarningPath)

    var coroutineScope = CoroutineScope(Dispatchers.Main)

    private val algorithmsLabel = label {
        val listOfKind = mutableListOf<String>()
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }
        style = "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.mainFontSize};"

        controller.projectAfterPartialParsing.onChange {
            it?.let {
                it.projectFrames.forEach { frame ->
                    getListOfAlgorithms(frame, listOfKind)
                }
                this.text = "Algorithms: " + listOfKind.toStringWithoutBrackets()
                tooltip(listOfKind.toStringWithoutBrackets()).apply {
                    style =
                        "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.tooltipFontSize}; -fx-background-color: #${Style.surfaceColor}; -fx-text-fill: #707070;"
                }
            }
        }
    }

    private val importButton = mfxButton("IMPORT") {
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }
        maxWidth = 75.0
        prefHeight = 23.0
        style = buttonStyle
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
                importAction(this@mfxButton, projectVal)
            }
        }
    }

    private val cancelButton = mfxButton("CANCEL") {
        maxWidth = 75.0
        prefHeight = 23.0
        style = buttonStyle
        textFill = Paint.valueOf(Style.primaryColor)
        prefWidth = ButtonWidth
        action {
            cancelAction()
        }
    }

    private val countImagesLabel = label {
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }

        style = "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.mainFontSize};"
        controller.projectAfterPartialParsing.onChange {
            val countFiles = it?.projectFrames?.count()
            this.text = "$countFiles images found"
            this.graphic = ImageView(filesCountIcon)
        }
    }

    private val countErrorsLabel = label {
        visibleWhen { controller.projectAfterPartialParsing.isNotNull }

        style = "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.mainFontSize};"
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
            hbox(10) {
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
        MaterialFXDialog.changeContent(menuBarView.content, loading.root)
    }

    private fun importAction(button: MFXButton, projectVal: Project) {
        try {
            mainController.visualizeProject(projectVal.layers, projectVal.frames)
            mainController.displayCatalogueFrames(projectVal.frames)
            button.isDisable = true

            menuBarView.dialog.close()
        } catch (e: Exception) {
            createAlertForError("Visualization error")
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

    companion object {
        private const val ButtonWidth = 180.0
    }
}
