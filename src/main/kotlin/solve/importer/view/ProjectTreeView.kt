package solve.importer.view


import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.image.ImageView
import solve.importer.ProjectParser
import solve.importer.model.ColumnData
import solve.importer.model.FileInTree
import solve.utils.loadImage
import tornadofx.*

open class ProjectTreeView : View() {

    var rootTree = TreeItem(FileInTree(ColumnData("", false)))

    private val directoryPathView: DirectoryPathView by inject()

    private fun updateTree() {
        directoryPathView.path.onChange {
            if (!it.isNullOrEmpty()) {
                rootTree.children.remove(0, rootTree.children.size)
                if (directoryPathView.project.value != null) {
                    rootTree = ProjectParser.createTreeWithFiles(directoryPathView.project.value, rootTree)
                }
            }
        }
    }

    override val root = treetableview(rootTree) {
        visibleWhen { directoryPathView.project.isNotNull }

        this.isShowRoot = false

        val filesColumn: TreeTableColumn<FileInTree, ColumnData> = TreeTableColumn<FileInTree, ColumnData>().apply {
            isResizable = false
            prefWidth = 220.0
        }
        val errorsColumn: TreeTableColumn<FileInTree, ColumnData> = TreeTableColumn<FileInTree, ColumnData>().apply {
            isResizable = false
            prefWidth = 150.0
        }

        filesColumn.cellValueFactory = TreeItemPropertyValueFactory("name")
        errorsColumn.cellValueFactory = TreeItemPropertyValueFactory("error")

        filesColumn.setCellFactory { _ ->
            object : TreeTableCell<FileInTree, ColumnData?>() {
                private val imageIcon = loadImage("icons/importer/photo.png")
                private val fileIcon = loadImage("icons/importer/description.png")
                private val errorFolderIcon = loadImage("icons/importer/error_folder.png")
                private val errorFileIcon = loadImage("icons/importer/error_file.png")

                override fun updateItem(item: ColumnData?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty) null else item!!.name

                    graphic = if (empty) null
                    else {
                        if (item!!.isLeaf) {
                            if (item.error.isEmpty()) {
                                ImageView(fileIcon)
                            } else {
                                ImageView(errorFileIcon)
                            }
                        } else {
                            if (item.error.isEmpty()) {
                                ImageView(imageIcon)
                            } else {
                                ImageView(errorFolderIcon)
                            }

                        }
                    }
                }
            }
        }

        errorsColumn.setCellFactory {
            object : TreeTableCell<FileInTree, ColumnData?>() {
                override fun updateItem(item: ColumnData?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        text = null
                    } else {
                        text = item!!.name.replace("[", "").replace("]", "")
                        tooltip(text).apply {

                        }
                    }
                }
            }
        }
        this.columns.addAll(filesColumn, errorsColumn)
        updateTree()
    }
}
