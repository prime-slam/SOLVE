package solve.importer.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.OverrunStyle
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.DirectoryChooser
import solve.importer.ProjectParser.parseDirectory
import solve.importer.controller.ImporterController
import solve.utils.loadImage
import tornadofx.*
import java.io.File

class DirectoryPathView : View() {
    private val controller: ImporterController by inject()

    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }

    private val filesCountIcon = loadImage("icons/importer/check_circle.png")
    private val errorsCountIcon = loadImage("icons/importer/warning.png")

    private val directoryLabel = label("Project directory") {
        maxWidth = 300.0
        textOverrun = OverrunStyle.ELLIPSIS
        controller.directoryPath.onChange {
            tooltip(controller.directoryPath.value)
            val x = controller.directoryPath.value
            this.text = if (x == null) "Project directory"
            else "Project directory\n$x"
        }
    }

    private val changeButton = button("Change") {
        action {
            val dir = directoryChooser.showDialog(currentStage)
            controller.directoryPath.set(dir?.absolutePath)
        }
    }

    private val countImagesLabel = label {
        visibleWhen { controller.project.isNotNull }
        controller.project.onChange {
            val countFiles = it?.frames?.count()
            this.text = "$countFiles images found"
            this.graphic = ImageView(filesCountIcon)
        }
    }

    private val countErrorsLabel = label {
        visibleWhen { controller.project.isNotNull }
        padding = Insets(3.0, 0.0, 5.0, 0.0)
        controller.project.onChange {
            var countErrors = 0
            it?.frames?.forEach { frame ->
                if (frame.isImageErrored)
                    countErrors += 1
            }
            this.text = "$countErrors errors"
            this.graphic = ImageView(errorsCountIcon)
        }
    }

    override val root =
        vbox {
            controller.directoryPath.onChange {
                if (!it.isNullOrEmpty()) {
                    val tempProject = parseDirectory(it.toString())
                    if (tempProject == null) {
                        controller.directoryPath.set(null)
                    }
                    controller.project.set(tempProject)
                    if (controller.directoryPath.value != null) {
                        directoryChooser.initialDirectory = File(controller.directoryPath.value)
                    }
                }
            }

            borderpane {
                left {
                    add(directoryLabel)
                }
                right {
                    add(changeButton.apply {
                        BorderPane.setAlignment(this, Pos.CENTER_RIGHT)
                    })
                }

            }
            add(countImagesLabel)
            add(countErrorsLabel)
        }
}