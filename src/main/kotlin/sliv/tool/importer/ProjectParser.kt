package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import java.io.File

object ProjectParser {
    const val img = "images"

    val files = FXCollections.observableHashMap<Long, MutableList<OutputInfo>>()
    val images = FXCollections.observableArrayList<ImageInfo>()

    fun createTreeWithFiles(path: String?, tree: TreeItem<String>): TreeItem<String> {
        if (path == null) {
            throw Exception("Directory not selected")
        }
        var directory = File(path)

        for (folder in directory.listFiles()) {
            if (folder.name == img) {
                images.addAll(folder.listFiles().map {
                    ImageInfo(it.nameWithoutExtension, it.absolutePath)
                })
            } else {
                folder.listFiles().map {
                    var key = it.nameWithoutExtension.toLong()
                    if (files.containsKey(key)) {
                        files.getValue(key).add(OutputInfo(folder.nameWithoutExtension, it.absolutePath))
                    } else {
                        files[key] = mutableListOf(OutputInfo(folder.nameWithoutExtension, it.absolutePath))
                    }
                }
            }
        }

        tree.value = directory.name
        files.map { thisImg ->
            var img = TreeItem(thisImg.key.toString())
            img.children.addAll(thisImg.value.map {
                TreeItem(it.algo)
            })
            tree.children.add(img)
        }
        return tree
    }
}