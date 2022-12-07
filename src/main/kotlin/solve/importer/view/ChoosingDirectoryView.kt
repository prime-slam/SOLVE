package solve.importer.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.OverrunStyle
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.DirectoryChooser
import solve.importer.ProjectParser
import solve.importer.ProjectParser.parseDirectory
import solve.importer.controller.ImporterController
import solve.main.MainController
import solve.menubar.view.MenuBarView
import solve.project.model.Project
import tornadofx.*
import java.io.File

class ChoosingDirectoryView : Fragment() {
    private val controller: ImporterController by inject()
    private val mainController: MainController by inject()
    private val path = SimpleStringProperty()
    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }
    private val project = objectProperty<Project>()

    override val root = borderpane {
        var rootTree = TreeItem("").apply { isExpanded = true }
        path.bindBidirectional(controller.directoryPath)
        project.bindBidirectional(controller.project)
        top {
            vbox {
                borderpane {
                    left {
                        vbox {
                            label("Project directory")
                            label {
                                maxWidth = 300.0
                                textOverrun = OverrunStyle.ELLIPSIS
                                path.onChange {
                                    tooltip(path.value)
                                }
                                bind(controller.directoryPath)
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
                        this.text = "${countFiles} images found"
                        this.graphic = ImageView(Image("file:icons/check_circle.png"))
                    }

                }
                label {
                    visibleWhen { path.isNotEmpty }
                    padding = Insets(3.0, 0.0, 5.0, 0.0)
                    project.onChange {
                        var countErrors = 0
                        it?.frames?.forEach {
                            countErrors += it.errorMsg.count()
                        }
                        this.text = "${countErrors} errors"
                        this.graphic = ImageView(Image("file:icons/warning.png"))
                    }
                }
            }

        }
        path.onChange {
            if (!it.isNullOrEmpty()) {
                project.set(parseDirectory(it.toString()))
                directoryChooser.initialDirectory = File(path.value)
                rootTree.children.remove(0, rootTree.children.size)
                rootTree = ProjectParser.createTreeWithFiles(project.value, rootTree)
            }
        }
        center {
            padding = Insets(5.0, 0.0, 0.0, 0.0)
            treeview(rootTree) {
                visibleWhen { path.isNotEmpty }
            }
        }

        bottom {
            hbox(10) {
                padding = Insets(10.0, 0.0, 0.0, 0.0)
                button("Cancel") {
                    action {
                        controller.directoryPath.set(null)
                        MenuBarView().importer.close()
                    }
                    prefWidth = 180.0
                }
                button("Import") {
                    prefWidth = 180.0
                    isDisable = true
                    project.onChange {
                        if (project.value.frames.isNotEmpty()) {
                            isDisable = false
                        }
                    }
                    action {
                        val projectVal = project.value
                        mainController.sceneFacade.visualize(projectVal.layers, projectVal.frames)
                        mainController.displayCatalogueFrames(projectVal.frames)
                        MenuBarView().importer.close()
                    }
                }
            }
        }

    }
}