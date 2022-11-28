package sliv.tool.importer.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.TreeItem
import javafx.stage.DirectoryChooser
import sliv.tool.importer.ProjectParser
import sliv.tool.importer.ProjectParser.parseDirectory
import sliv.tool.importer.controller.ImporterController
import sliv.tool.main.MainController
import sliv.tool.project.model.Project
import tornadofx.*
import sliv.tool.menubar.view.MenuBarView

class ChoosingDirectoryView : Fragment() {
    private val controller: ImporterController by inject()
    private val mainController: MainController by inject()
    private val path = SimpleStringProperty()
    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }
    private val project = objectProperty<Project>()

    override val root = vbox(20) {
        var rootTree = TreeItem("Project not selected").apply { isExpanded = true }
        padding = Insets(5.0, 10.0, 0.0, 10.0)
        path.bindBidirectional(controller.directoryPath)
        project.bindBidirectional(controller.project)
        hbox(20) {
            label {
                bind(controller.directoryPath)
            }
            button("Change") {
                action {
                    val dir = directoryChooser.showDialog(currentStage)
                    controller.directoryPath.set(dir.absolutePath)
                }
            }
        }

        path.onChange {
            project.set(parseDirectory(it.toString()))
            rootTree = ProjectParser.createTreeWithFiles(project.value, rootTree)
        }

        treeview(rootTree) {
            visibleWhen { path.isNotEmpty }
        }

        vbox {
            padding = Insets(0.0, 0.0, 0.0, 10.0)
            button("Import") {
                this.isDisable = true
                project.onChange {
                    if (project.value.frames.isNotEmpty()) {
                        this.isDisable = false
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