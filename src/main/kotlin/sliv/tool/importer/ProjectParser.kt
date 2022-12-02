package sliv.tool.importer

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
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

    private fun selectKind(kindString: String): LayerKind? {
        return when (kindString) {
            "keypoint" -> LayerKind.KEYPOINT
            "plane" -> LayerKind.PLANE
            "line" -> LayerKind.LINE
            else -> return null
        }
    }

    private fun parseOutputs(
        folder: File,
        layers: ObservableList<ProjectLayer>,
        landmarks: ObservableMap<Long, MutableList<LandmarkFile>>,
        errorFolders: MutableList<String>
    ) {
        val directoryName = folder.name
        val indexOfSeparator = directoryName.lastIndexOf("_")
        if (indexOfSeparator == -1) {
            errorFolders.add(directoryName)
            return
        }
        val name = directoryName.substring(0, indexOfSeparator)
        val kindString = directoryName.substring(indexOfSeparator + 1)

        val kind = selectKind(kindString)
        if (kind != null) {
            layers.add(ProjectLayer(kind, name))
        } else {
            errorFolders.add(directoryName)
            return
        }
        val errorOutputs = mutableListOf<String>()
        folder.listFiles()?.forEach {
            val imageName = it.nameWithoutExtension
            try {
                val longName = imageName.toLong()
                landmarks.putIfAbsent(
                    longName,
                    mutableListOf()
                )
                landmarks[longName]!!.add(LandmarkFile(layers.last(), Path(it.path), extractUIDs(it.path)))
            } catch (e: Exception) {
                errorOutputs.add(imageName)
            }
        }
        if (errorOutputs.isNotEmpty()) {
            alert(
                Alert.AlertType.WARNING,
                "$errorOutputs are incorrect names of files. They can't be converted to Long"
            )
        }
    }

    private fun parseImages(folder: File, images: ObservableMap<Long, String>) {
        val errorImages = mutableListOf<String>()
        folder.listFiles()?.forEach {
            val imageName = it.nameWithoutExtension
            try {
                val longName = imageName.toLong()
                images[longName] = it.absolutePath
            } catch (e: Exception) {
                errorImages.add(imageName)
            }
        }
        if (errorImages.isNotEmpty()) {
            alert(
                Alert.AlertType.WARNING,
                "$errorImages is incorrect name of file. It is not converted to Long"
            )
        }
    }

    fun parseDirectory(path: String): Project {
        val directory = File(path)
        val images = FXCollections.observableHashMap<Long, String>()
        val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
        val frames = FXCollections.observableArrayList<ProjectFrame>()
        val layers = FXCollections.observableArrayList<ProjectLayer>()
        val errorFolders = mutableListOf<String>()

        for (folder in directory.listFiles()) {
            if (folder.name != IMAGE_DIRECTORY_NAME) {
                parseOutputs(folder, layers, landmarks, errorFolders)
            } else {
                parseImages(folder, images)
            }
        }
        if (errorFolders.isNotEmpty()) {
            alert(
                Alert.AlertType.WARNING,
                "The directories $errorFolders are skipped, because names of folders don't contain correct kind name."
            )
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