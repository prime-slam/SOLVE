package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import sliv.tool.parsers.planes.ImagePlanesParser.extractUIDs
import sliv.tool.project.model.LandmarkFile
import sliv.tool.project.model.LayerKind
import sliv.tool.project.model.ProjectLayer
import java.io.File
import kotlin.io.path.Path

object ProjectParser {
    const val img = "images"

    fun createTreeWithFiles(path: String?, tree: TreeItem<String>): TreeItem<String> {
        val files = FXCollections.observableArrayList<Triple<String, String, String>>()
        val images = FXCollections.observableArrayList<Pair<String, String>>()

        if (path == null) {
            throw Exception("Directory not selected")
        }

        for (folder in File(path).listFiles()) {
            if (folder.name == img) {
                for (file in folder.listFiles()){
                    images.add(file.nameWithoutExtension to file.absolutePath)
                }}
            else{
                for (file in folder.listFiles()){
                    files.add(Triple(folder.nameWithoutExtension, file.nameWithoutExtension, file.absolutePath))
                }
            }
        }

        tree.value = path.split("/").last()
        images.map{ thisImg ->
            var img = TreeItem(thisImg.first)
            files.map{ thisFile ->
                if (thisImg.first == thisFile.second) {
                    img.children.add(TreeItem(thisFile.first))
                }
            }
            tree.children.add(img)

        }
        return tree
    }

    fun parse() {
        val layers = FXCollections.observableArrayList<ProjectLayer>()
        val landmarks = FXCollections.observableArrayList<LandmarkFile>()
        val files = FXCollections.observableArrayList<Triple<String, String, String>>()
        files.forEach{
             var name = it.first
             var kind = it.second.split("_")[1]
             when(kind){
                 "keypoint" -> layers.add(ProjectLayer(LayerKind.KEYPOINT, name))
                 "plane" -> layers.add(ProjectLayer(LayerKind.PLANE, name))
                 "line" -> layers.add(ProjectLayer(LayerKind.LINE, name))
             }
             landmarks.add(LandmarkFile(layers.last(), Path(it.third), extractUIDs(it.third) ))
         }
    }
}