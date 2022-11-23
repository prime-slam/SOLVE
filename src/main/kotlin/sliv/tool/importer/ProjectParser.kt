package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import java.io.File

object ProjectParser {
    const val IMAGE_DIRECTORY_NAME = "images"

    val files = FXCollections.observableHashMap<Long, MutableList<OutputInfo>>()
    val images = FXCollections.observableArrayList<ImageInfo>()

    fun createTreeWithFiles(path: String?, tree: TreeItem<String>): TreeItem<String> {
        if (path == null) {
            return tree
        }
        var directory = File(path)

        for (folder in directory.listFiles()) {
            if (folder.name == IMAGE_DIRECTORY_NAME) {
                images.addAll(folder.listFiles().map {
                    ImageInfo(it.nameWithoutExtension, it.absolutePath)
                })
            } else {
                folder.listFiles().map {
                    var key = it.nameWithoutExtension.toLong()
                    if (files.containsKey(key)) {
                        files[key]?.add(OutputInfo(folder.nameWithoutExtension, it.absolutePath))
                    } else {
                        files[key] = mutableListOf(OutputInfo(folder.nameWithoutExtension, it.absolutePath))
                    }
                }
            }
        }

        tree.value = directory.name
        files.map { (name, outputs) ->
            var imageNode = TreeItem(name.toString())
            imageNode.children.addAll(outputs.map {
                TreeItem(it.algo)
            })
            tree.children.add(imageNode)
        }
        return tree
    }
}