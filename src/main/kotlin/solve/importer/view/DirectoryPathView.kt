package solve.importer.view

import io.github.palexdev.materialfx.effects.DepthLevel
import io.github.palexdev.materialfx.enums.FloatMode
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import solve.importer.ProjectParser
import solve.importer.controller.ImporterController
import solve.styles.Style
import solve.utils.mfxButton
import solve.utils.mfxTextField
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
        textFill = Color.valueOf(Style.onBackgroundColor)

        prefHeight = 48.0
        prefWidth = 280.0
        BorderPane.setAlignment(this, Pos.CENTER)
        BorderPane.setMargin(this, Insets(16.0, 0.0, 0.0, 0.0))

        style = "-fx-border-color: #${Style.primaryColorLight}; -fx-font-size: ${Style.mainFontSize}; " +
            "-fx-font-family: ${Style.fontCondensed}"

        floatMode = FloatMode.BORDER
        floatingText = "Project directory"

        controller.projectAfterPartialParsing.onChange {
            if (controller.projectAfterPartialParsing.value != null) {
                tooltip(controller.directoryPath.value).apply {
                    style = "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.tooltipFontSize}; " +
                        "-fx-background-color: #${Style.surfaceColor}; -fx-text-fill: #707070;"
                }
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

        style = "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.headerFontSize}; "
        VBox.setMargin(this, Insets(0.0, 0.0, 0.0, 0.0))
    }

    private val selectButton = mfxButton("SELECT") {
        style = "-fx-border-color: #${Style.primaryColor};  -fx-font-size: ${Style.buttonFontSize}; " +
            "-fx-font-family: ${Style.fontCondensed}; -fx-font-weight:700; -fx-border-radius: 4px; " +
            "-fx-text-fill: #${Style.primaryColor};"
        BorderPane.setAlignment(this, Pos.CENTER)
        BorderPane.setMargin(this, Insets(15.0, 0.0, 0.0, 0.0))
        textFill = Color.valueOf(Style.primaryColor)
        alignment = Pos.CENTER
        depthLevel = DepthLevel.LEVEL1
        prefHeight = 31.0
        prefWidth = 98.0
        action {
            val dir = directoryChooser.showDialog(currentStage)
            controller.directoryPath.set(dir?.absolutePath)
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
            style = "-fx-background-color: #${Style.surfaceColor}"
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
}
