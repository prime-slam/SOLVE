package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import java.io.File

object ProjectParser {
    const val img = "images"

    fun createTreeWithFiles(path: String?, tree: TreeItem<String>): TreeItem<String> {
        val files = FXCollections.observableArrayList<OutputInfo>()
        val images = FXCollections.observableArrayList<ImageInfo>()

        if (path == null) {
            throw Exception("Directory not selected")
        }

        for (folder in File(path).listFiles()) {
            if (folder.name == img) {
                images.addAll(folder.listFiles().map {
                    ImageInfo(it.nameWithoutExtension, it.absolutePath)
                })
            } else {
                files.addAll(folder.listFiles().map {
                    OutputInfo(it.nameWithoutExtension, folder.nameWithoutExtension, it.absolutePath)
                })
            }
        }

        tree.value = path.split("/").last()
        images.map { thisImg ->
            var img = TreeItem(thisImg.name)
            files.map { thisFile ->
                if (thisImg.name == thisFile.name) {
                    img.children.add(TreeItem(thisFile.algo))
                }
            }
            tree.children.add(img)
        }
        return tree
    }
}