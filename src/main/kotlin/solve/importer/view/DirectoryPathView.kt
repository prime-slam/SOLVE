package solve.importer.view

import io.github.palexdev.materialfx.effects.DepthLevel
import io.github.palexdev.materialfx.enums.FloatMode
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import solve.importer.ProjectParser
import solve.importer.controller.ImporterController
import solve.styles.Style
import solve.utils.materialfx.mfxButton
import solve.utils.materialfx.mfxTextField
import tornadofx.*
import java.io.File

class DirectoryPathView : View() {
    private val controller: ImporterController by inject()

    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }

    private val directoryField = mfxTextField {
        val empty = text
        isEditable = false
        isAllowEdit = false

        isDisable = true
        textFill = Color.valueOf(Style.OnBackgroundColor)

        prefHeight = 48.0
        prefWidth = 280.0
        BorderPane.setAlignment(this, Pos.CENTER)
        BorderPane.setMargin(this, Insets(16.0, 0.0, 0.0, 0.0))

        style = "-fx-border-color: #${Style.PrimaryColorLight}; -fx-font-size: ${Style.MainFontSize}; " +
            "-fx-font-family: ${Style.FontCondensed}"

        floatMode = FloatMode.BORDER
        floatingText = "Project directory"

        controller.projectAfterPartialParsing.onChange {
            if (controller.projectAfterPartialParsing.value != null) {
                tooltip(controller.directoryPath.value)
            }
            text = if (it != null) {
                controller.directoryPath.value
            } else {
                empty
            }
        }
    }

    private val labelName = label("Import a directory") {
        hgrow = Priority.ALWAYS
        prefHeight = 0.0
        prefWidth = 200.0

        style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.HeaderFontSize}; "

        VBox.setMargin(this, Insets(0.0, 0.0, 0.0, 0.0))
    }

    private val selectButton = mfxButton("SELECT") {
        style = "-fx-border-color: #${Style.PrimaryColor};  -fx-font-size: ${Style.ButtonFontSize}; " +
            "-fx-font-family: ${Style.FontCondensed}; -fx-font-weight:700; -fx-border-radius: 4px; " +
            "-fx-text-fill: #${Style.PrimaryColor};"
        BorderPane.setAlignment(this, Pos.CENTER)
        BorderPane.setMargin(this, Insets(15.0, 0.0, 0.0, 0.0))
        textFill = Color.valueOf(Style.PrimaryColor)
        alignment = Pos.CENTER
        depthLevel = DepthLevel.LEVEL1
        isFocusTraversable = false
        prefHeight = 31.0
        prefWidth = 98.0
        tooltip("Ctrl+O")
        action {
            chooseDirectoryAction()
        }
    }

    init {
        accelerators[KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN)] = {
            chooseDirectoryAction()
        }
    }

    override val root =
        vbox {
            padding = Insets(0.0, 24.0, 0.0, 24.0)
            controller.directoryPath.onChange {
                if (!it.isNullOrEmpty()) {
                    val projectAfterPartialParsing = ProjectParser.partialParseDirectory(it.toString())
                    if (projectAfterPartialParsing == null) {
                        controller.directoryPath.set(null)
                    }
                    controller.projectAfterPartialParsing.set(projectAfterPartialParsing)
                    if (controller.directoryPath.value != null) {
                        directoryChooser.initialDirectory = File(controller.directoryPath.value)
                    }
                }
            }
            style = "-fx-background-color: #${Style.SurfaceColor}"
            BorderPane.setAlignment(this, Pos.CENTER)
            add(labelName)
            borderpane {
                padding = Insets(0.0, 0.0, 10.0, 0.0)
                prefHeight = 0.0
                prefWidth = 453.0

                right {
                    add(selectButton)
                }
                left {
                    add(directoryField)
                }
            }
            separator().apply { visibleWhen { controller.projectAfterPartialParsing.isNotNull } }
        }

    private fun chooseDirectoryAction() {
        val dir = directoryChooser.showDialog(currentStage)
        controller.directoryPath.set(dir?.absolutePath)
    }
}
