package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import sliv.tool.parsers.planes.ImagePlanesParser.extractUIDs
import sliv.tool.project.model.*
import java.io.File
import kotlin.io.path.Path

object ProjectParser {
    private const val IMAGE_DIRECTORY_NAME = "images"
    private val images = FXCollections.observableHashMap<Long, String>()
    private val layers = FXCollections.observableArrayList<ProjectLayer>()
    private val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
    private val frames = FXCollections.observableArrayList<ProjectFrame>()

    fun parseDirectory(path: String): Project {
        if (path == null) {
            throw Exception("Directory not selected")
        }
        var directory = File(path)

        for (folder in directory.listFiles()) {
            if (folder.name != IMAGE_DIRECTORY_NAME) {
                var splitName = folder.name.split("_")
                var name = splitName[0]
                var kind = splitName[1]
                when (kind) {
                    "keypoint" -> layers.add(ProjectLayer(LayerKind.KEYPOINT, name))
                    "plane" -> layers.add(ProjectLayer(LayerKind.PLANE, name))
                    "line" -> layers.add(ProjectLayer(LayerKind.LINE, name))
                }
                folder.listFiles().map {
                    if (landmarks.containsKey(it.nameWithoutExtension.toLong())) {
                        landmarks[it.nameWithoutExtension.toLong()]?.add(
                            LandmarkFile(
                                layers.last(),
                                Path(it.path),
                                extractUIDs(it.path)
                            )
                        )
                    } else {
                        landmarks.put(
                            it.nameWithoutExtension.toLong(),
                            mutableListOf(LandmarkFile(layers.last(), Path(it.path), extractUIDs(it.path)))
                        )
                    }
                }
            } else {
                folder.listFiles().map {
                    images.put(it.nameWithoutExtension.toLong(), it.absolutePath)
                }
            }
        }
        landmarks.forEach { (name, outputs) ->
            frames.add(ProjectFrame(name, Path(images[name].toString()), outputs))
        }
        return Project(Path(path!!), frames, layers)
    }

    fun createTreeWithFiles(project: Project, tree: TreeItem<String>): TreeItem<String> {
        val imgIcon = Image("file:icons/photo.png")
        val fileIcon = Image("file:icons/description.png")

        var directory = File(project.currentDirectory.toString())

        tree.value = directory.name
        project.frames.map {
            var imageNode = TreeItem(it.timestamp.toString(), ImageView(imgIcon))
            imageNode.children.addAll(it.landmarkFiles.map { landmark ->
                var layer = landmark.projectLayer
                var fileName = layer.name + "_" + layer.kind.toString().lowercase()
                TreeItem(fileName, ImageView(fileIcon))
            })
            tree.children.add(imageNode)
        }
        return tree
    }
}