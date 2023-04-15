package solve.importer.view

import io.github.palexdev.materialfx.effects.DepthLevel
import io.github.palexdev.materialfx.enums.FloatMode
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
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

    private val roboto = Style.fontCondensed

    private val directoryField = mfxTextField {
        isEditable = false
        isAllowEdit = false

        enableWhen {
            controller.projectAfterPartialParsing.isNotNull
        }
        textFill = Color.valueOf(Style.onBackgroundColor)

        prefHeight=48.0
        prefWidth=280.0
        BorderPane.setAlignment(this, Pos.CENTER)
        BorderPane.setMargin(this, Insets(16.0, 0.0,0.0, 0.0))

        style = "-fx-border-color: #${Style.primaryColorLight}; -fx-font-size: 15px;"

        floatMode = FloatMode.BORDER
        font = Font.font(roboto, 14.0)

        floatingText = "Select project directory"
        controller.directoryPath.onChange {
            floatingText = if (it != null && it != ""){
                "Selected project directory"
            } else{
                "Select project directory"
            }
        }

        controller.projectAfterPartialParsing.onChange {
            if (controller.projectAfterPartialParsing.value != null){
                tooltip(controller.directoryPath.value).apply {
                    font = Font.font(Style.font, 12.0)
                    style = "-fx-background-color: #${Style.surfaceColor}; -fx-text-fill: #707070;"
                }
            }
            text = if (it != null){
                controller.directoryPath.value
            } else{
                ""
            }
        }
    }

    private val labelName = label("Import a directory") {
        prefHeight=0.0
        prefWidth=141.0
        font = Font.font(Style.fontCondensed, 20.0)
        VBox.setMargin(this, Insets(0.0, 0.0, 0.0, 0.0))
    }

    private val selectButton = mfxButton ("SELECT"){
        style="-fx-border-color: #${Style.primaryColor}; -fx-font-family: $roboto; -fx-font-size: ${Style.fontSize}; -fx-font-weight: BOLD; -fx-border-radius: 4px; -fx-text-fill: #${Style.primaryColor};"
        BorderPane.setAlignment(this, Pos.CENTER)
        BorderPane.setMargin(this, Insets(15.0, 0.0, 0.0, 0.0))
        textFill = Color.valueOf(Style.primaryColor)

        alignment=Pos.CENTER
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
                padding = Insets(0.0, 0.0, 10.0,0.0)
                prefHeight=0.0
                prefWidth=453.0

                right {
                    add(selectButton)
                }
                left{
                    add(directoryField)
                }
            }
            separator().apply { visibleWhen { controller.projectAfterPartialParsing.isNotNull } }
        }
}
