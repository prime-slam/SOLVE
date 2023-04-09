package solve.importer.view

import javafx.geometry.Insets
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import solve.constants.IconsImporterDescriptionPath
import solve.constants.IconsImporterErrorFilePath
import solve.constants.IconsImporterErrorFolderPath
import solve.constants.IconsImporterPhotoPath
import solve.importer.ProjectParser
import solve.importer.controller.ImporterController
import solve.importer.model.FileInTree
import solve.importer.model.FileInfo
import solve.utils.loadResourcesImage
import solve.utils.toStringWithoutBrackets
import tornadofx.*

open class ProjectTreeView : View() {
    private var rootTree = TreeItem(FileInTree(FileInfo("", false)))

    private val controller: ImporterController by inject()

    private fun updateTree() {
        controller.directoryPath.onChange {
            if (!it.isNullOrEmpty()) {
                rootTree.children.clear()
                if (controller.projectAfterPartialParsing.value != null) {
                    rootTree = ProjectParser.createTreeWithFiles(controller.projectAfterPartialParsing.value, rootTree)
                }
            }
        }
    }

    override val root =
        treetableview(rootTree) {
            style =
                "-fx-font-family: Roboto Condensed; -fx-font-weight: REGULAR; -fx-text-fill: #78909C; -fx-font-size: 14px;"
            BorderPane.setMargin(this, Insets(0.0, 0.0, 2.0, 15.0))

            visibleWhen { controller.projectAfterPartialParsing.isNotNull }
            this.isShowRoot = false

            val filesColumn: TreeTableColumn<FileInTree, FileInfo> = TreeTableColumn<FileInTree, FileInfo>().apply {
                isResizable = false
                prefWidth = 220.0
            }
            val errorsColumn: TreeTableColumn<FileInTree, FileInfo> = TreeTableColumn<FileInTree, FileInfo>().apply {
                isResizable = false
                prefWidth = 150.0
            }

            filesColumn.cellValueFactory = TreeItemPropertyValueFactory("file")
            errorsColumn.cellValueFactory = TreeItemPropertyValueFactory("file")

            filesColumn.setCellFactory { _ ->
                object : TreeTableCell<FileInTree, FileInfo?>() {
                    private val imageIcon = loadResourcesImage(IconsImporterPhotoPath)
                    private val fileIcon = loadResourcesImage(IconsImporterDescriptionPath)
                    private val errorFolderIcon = loadResourcesImage(IconsImporterErrorFolderPath)
                    private val errorFileIcon = loadResourcesImage(IconsImporterErrorFilePath)

                    override fun updateItem(item: FileInfo?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (empty) null else item?.name
                        graphic = if (empty) {
                            null
                        } else {
                            if (item != null) {
                                if (item.isLeaf) {
                                    if (item.errors.isEmpty()) {
                                        ImageView(fileIcon)
                                    } else {
                                        ImageView(errorFileIcon)
                                    }
                                } else {
                                    if (item.errors.isEmpty()) {
                                        ImageView(imageIcon).apply {
                                            fitHeight = 16.0
                                            fitWidth = 16.0

                                        }
                                    } else {
                                        ImageView(errorFolderIcon)
                                    }
                                }
                            } else {
                                ImageView()
                            }
                        }
                    }
                }
            }

            errorsColumn.setCellFactory {
                object : TreeTableCell<FileInTree, FileInfo?>() {
                    override fun updateItem(item: FileInfo?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (empty) {
                            null
                        } else item?.errors?.toStringWithoutBrackets()
                        if (!empty && text.isNotEmpty()) {
                            tooltip(text)
                        }
                    }
                }
            }

            this.columns.addAll(filesColumn, errorsColumn)
            updateTree()
        }
}
