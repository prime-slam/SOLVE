package solve.importer.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.OverrunStyle
import javafx.scene.image.ImageView
import javafx.stage.DirectoryChooser
import solve.importer.ProjectParser.parseDirectory
import solve.importer.controller.ImporterController
import solve.project.model.Project
import solve.utils.loadImage
import tornadofx.*
import java.io.File

class DirectoryPathView : View() {
    private val controller: ImporterController by inject()
    val path = SimpleStringProperty()
    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }
    val project = objectProperty<Project>()

    private val filesCountIcon = loadImage("icons/importer/check_circle.png")

    private val errorsCountIcon = loadImage("icons/importer/warning.png")

    override val root =
        vbox {
            path.bindBidirectional(controller.directoryPath)
            project.bindBidirectional(controller.project)
            path.onChange {
                if (!it.isNullOrEmpty()) {
                    val tempProject = parseDirectory(it.toString())
                    if (tempProject == null){
                        controller.directoryPath.set(null)
                    }
                    project.set(tempProject)
                    directoryChooser.initialDirectory = File(path.value)
                }
            }
            borderpane {
                left {
                    label("Project directory") {
                        maxWidth = 300.0
                        textOverrun = OverrunStyle.ELLIPSIS
                        path.onChange {
                            tooltip(path.value)
                            val x = controller.directoryPath.value
                            if (x == null) {
                                this.text = "Project directory"
                            } else {
                                this.text = "Project directory\n$x"
                            }
                        }
                    }
                }
                right {
                    button("Change") {
                        action {
                            val dir = directoryChooser.showDialog(currentStage)
                            if (dir != null) {
                                controller.directoryPath.set(dir.absolutePath)
                            }
                        }
                    }
                }
            }
            label {
                visibleWhen { path.isNotEmpty }
                project.onChange {
                    val countFiles = it?.frames?.count()
                    this.text = "$countFiles images found"
                    this.graphic = ImageView(filesCountIcon)
                }

            }
            label {
                visibleWhen { path.isNotEmpty }
                padding = Insets(3.0, 0.0, 5.0, 0.0)
                project.onChange {
                    var countErrors = 0
                    it?.frames?.forEach {frame ->
                        if (frame.isImageErrored)
                        countErrors += 1
                        if (frame.isOutputErrored){
                            frame.landmarkFiles.forEach{
                            }
                        }
                    }
                    this.text = "$countErrors errors"
                    this.graphic = ImageView(errorsCountIcon)
                }
            }
        }


}