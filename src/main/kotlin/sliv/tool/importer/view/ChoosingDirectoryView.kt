package sliv.tool.importer.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.TreeItem
import javafx.stage.DirectoryChooser
import sliv.tool.importer.ProjectParser
import sliv.tool.importer.controller.ImporterController
import tornadofx.*

class ChoosingDirectoryView : Fragment() {
    private val controller: ImporterController by inject()
    private val path = SimpleStringProperty()
    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }

    override val root = vbox(20) {
        var rootTree = TreeItem("").apply { isExpanded = true }
        padding = Insets(5.0, 10.0, 0.0, 10.0)
        path.bindBidirectional(controller.directoryPath)
        hbox(20){
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
            rootTree = ProjectParser.createTreeWithFiles(it, rootTree)
        }
        treeview(rootTree) {
        }
    }
}