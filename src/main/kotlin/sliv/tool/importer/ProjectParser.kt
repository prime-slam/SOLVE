package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import java.io.File

object ProjectParser {

    fun createTree (path: String?, tree: TreeItem<String>): TreeItem<String> {
        val files = FXCollections.observableArrayList<Pair<String, String>>()
        val images = FXCollections.observableArrayList<Pair<String, String>>()

        for (folder in File(path).listFiles()) {
            if (folder.name == "images") {
                for (file in folder.listFiles()){
                    images.add(Pair(file.nameWithoutExtension, file.absolutePath))
                }}
            else{
                for (file in folder.listFiles()){
                    files.add(Pair(folder.nameWithoutExtension, file.absolutePath))
                }
            }
        }
        if (path != null) {
            tree.value = path.split("\\").last()
        }
        images.forEach { thisImg ->
            var img = TreeItem(thisImg.first)
            files.forEach{ thisFile ->
                if (thisImg.first == File(thisFile.second).nameWithoutExtension) {
                    img.children.add(TreeItem(thisFile.first))
                }
            }
            tree.children.add(img)
        }
        return tree
    }
}