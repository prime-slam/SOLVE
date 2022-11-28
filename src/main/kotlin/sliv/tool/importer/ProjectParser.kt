package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.scene.control.Alert
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import sliv.tool.parsers.planes.ImagePlanesParser.extractUIDs
import sliv.tool.project.model.*
import tornadofx.alert
import java.io.File
import kotlin.io.path.Path

object ProjectParser {
    private const val IMAGE_DIRECTORY_NAME = "images"

    private fun selectKind(nameDirectory: String, n: Int): LayerKind {
        return when (nameDirectory.substring(n + 1)) {
            "keypoint" -> LayerKind.KEYPOINT
            "plane" -> LayerKind.PLANE
            "line" -> LayerKind.LINE
            else -> {
                throw Exception()
            }
        }
    }

    fun parseDirectory(path: String): Project {
        val directory = File(path)
        val images = FXCollections.observableHashMap<Long, String>()
        val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
        val frames = FXCollections.observableArrayList<ProjectFrame>()
        val layers = FXCollections.observableArrayList<ProjectLayer>()

        for (folder in directory.listFiles()) {
            if (folder.name != IMAGE_DIRECTORY_NAME) {
                val nameDirectory = folder.name
                val n = nameDirectory.lastIndexOf("_")
                val name = nameDirectory.substring(0, n)
                try {
                    val kind = selectKind(nameDirectory, n)
                    layers.add(ProjectLayer(kind, name))
                } catch (e: Exception) {
                    alert(
                        Alert.AlertType.WARNING,
                        "The directory $nameDirectory is skipped, because name of folder doesn't contain correct kind name."
                    )
                    continue
                }

                folder.listFiles().forEach {
                    val imageName = it.nameWithoutExtension
                    try {
                        val longName = imageName.toLong()
                        landmarks.putIfAbsent(
                            longName,
                            mutableListOf(LandmarkFile(layers.last(), Path(it.path), extractUIDs(it.path)))
                        )
                    } catch (e: Exception) {
                        alert(
                            Alert.AlertType.WARNING,
                            "$imageName is incorrect name of file. It is not converted to Long"
                        )
                    }

                }
            } else {
                folder.listFiles().forEach {
                    val imageName = it.nameWithoutExtension
                    try {
                        val longName = imageName.toLong()
                        images[longName] = it.absolutePath

                    } catch (e: Exception) {
                        alert(
                            Alert.AlertType.WARNING,
                            "$imageName is incorrect name of file. It is not converted to Long"
                        )
                    }

                }

            }
        }
        landmarks.forEach { (name, outputs) ->
            frames.add(ProjectFrame(name, Path(images[name]!!), outputs))
        }
        return Project(Path(path), frames, layers)
    }

    fun createTreeWithFiles(project: Project, tree: TreeItem<String>): TreeItem<String> {
        val imgIcon = Image("file:icons/photo.png")
        val fileIcon = Image("file:icons/description.png")
        val directory = File(project.currentDirectory.toString())

        tree.value = directory.name
        project.frames.forEach {
            val imageNode = TreeItem(it.timestamp.toString(), ImageView(imgIcon))
            imageNode.children.addAll(it.landmarkFiles.map { landmark ->
                val layer = landmark.projectLayer
                val fileName = layer.name + "_" + layer.kind.toString().lowercase()
                TreeItem(fileName, ImageView(fileIcon))
            })
            tree.children.add(imageNode)
        }
        return tree
    }


}