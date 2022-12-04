package sliv.tool.importer.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.TreeItem
import javafx.stage.DirectoryChooser
import sliv.tool.importer.ProjectParser
import sliv.tool.importer.ProjectParser.parseDirectory
import sliv.tool.importer.controller.ImporterController
import sliv.tool.main.MainController
import sliv.tool.menubar.view.MenuBarView
import sliv.tool.project.model.Project
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
        padding = Insets(5.0, 15.0, 0.0, 0.0)
        path.bindBidirectional(controller.directoryPath)
        project.bindBidirectional(controller.project)
        top {
            borderpane {
                left {
                    vbox {
                        label("Project directory")
                        label {
                            bind(controller.directoryPath)
                        }
                    }
                }
                right {
                    button("Change") {
                        action {
                            val dir = directoryChooser.showDialog(currentStage)
                            controller.directoryPath.set(dir.absolutePath)
                        }
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
            treeview(rootTree) {
                visibleWhen { path.isNotEmpty }
            }
        }
        bottom {
            borderpane {
                padding = Insets(10.0, 0.0, 0.0, 0.0)
                left {
                    button("Cancel") {
                        action {
                            controller.directoryPath.set(null)
                            MenuBarView().importer.close()
                        }
                        prefWidth = 180.0
                    }
                }
                right {
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
                            MenuBarView().importer.close()
                        }
                    }
                }
            }
        }
    }
}