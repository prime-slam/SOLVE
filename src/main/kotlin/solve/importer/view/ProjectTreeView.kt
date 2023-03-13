package solve.importer.view

import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import solve.importer.ProjectParser
import solve.importer.controller.ImporterController
import solve.importer.model.FileInfo
import solve.importer.model.FileInTree
import solve.styles.LightTheme
import solve.styles.ThemeController
import solve.utils.loadImage
import solve.utils.toStringWithoutBrackets
import tornadofx.*

open class ProjectTreeView : View() {
    private var rootTree = TreeItem(FileInTree(FileInfo("", false)))

    private val controller: ImporterController by inject()

    private val themeController: ThemeController by inject()

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

    override val root = treetableview(rootTree) {
        padding = Insets(0.0, 7.0, 0.0, 0.0)
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
                private val imageIcon = loadImage("icons/importer/photo.png")
                private val fileIcon = loadImage("icons/importer/description.png")
                private val errorFolderIcon = loadImage("icons/importer/error_folder.png")
                private val errorFileIcon = loadImage("icons/importer/error_file.png")

                private val imageIconDark = loadImage("icons/importer/photo_dark_theme.png")
                private val fileIconDark = loadImage("icons/importer/description_dark_theme.png")

                private fun chooseIcons(): Pair<Image?, Image?> {
                    return if (themeController.activeTheme == LightTheme::class){
                        Pair(imageIcon, fileIcon)
                    } else{
                        Pair(imageIconDark, fileIconDark)
                    }
                }

                override fun updateItem(item: FileInfo?, empty: Boolean) {
                    val icons = chooseIcons()
                    super.updateItem(item, empty)
                    text = if (empty) null else item?.name
                    graphic = if (empty) {
                        null
                    } else {
                        if (item != null) {
                            if (item.isLeaf) {
                                if (item.errors.isEmpty()) {
                                    ImageView(icons.first)
                                } else {
                                    ImageView(errorFileIcon)
                                }
                            } else {
                                if (item.errors.isEmpty()) {
                                    ImageView(icons.second)
                                } else {
                                    ImageView(errorFolderIcon)
                                }
                            }
                        }
                        else {
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