package solve.importer

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.control.Alert
import javafx.scene.control.TreeItem
import javafx.stage.Modality
import solve.importer.model.FileInfo
import solve.importer.model.FileInTree
import solve.importer.model.ImporterProject
import solve.importer.model.ImporterProjectFrame
import solve.importer.view.ImporterView
import solve.parsers.planes.ImagePlanesParser.extractUIDs
import solve.project.model.*
import tornadofx.FX.Companion.find
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.extension

object ProjectParser {
    private const val IMAGE_DIRECTORY_NAME = "images"

    private const val PNG_EXTENSION = "png"
    private const val JPG_EXTENSION = "jpg"
    private val possibleExtensions = setOf(PNG_EXTENSION, JPG_EXTENSION)

    private val importer = find<ImporterView>()

    private fun selectKind(kindString: String): LayerKind? {
        return when (kindString) {
            "keypoint" -> LayerKind.Keypoint
            "plane" -> LayerKind.Plane
            "line" -> LayerKind.Line
            else -> null
        }
    }

    fun createAlert(content: String) {
        val alert = Alert(Alert.AlertType.ERROR, content).apply {
            headerText = ""
            initModality(Modality.APPLICATION_MODAL)
            initOwner(importer.root.scene.window)
        }
        alert.show()
    }

    private fun incorrectExtensionError(frame: ImporterProjectFrame) {
        if (!possibleExtensions.contains(frame.frame.imagePath.extension)) {
            frame.isImageErrored = true
            frame.errorMessage?.add("The image has an incorrect extension")
        }
    }

    private fun noSomeAlgorithmError(frame: ImporterProjectFrame, layers: ObservableList<ProjectLayer>) {
        val diff = layers.minus(frame.frame.landmarkFiles.map { landmark ->
            landmark.projectLayer
        }.toSet())
        val diffString = diff.map { layer ->
            layer.name + "_" + layer.kind
        }

        if (diff.isNotEmpty()) {
            frame.isImageErrored = true
            if (diff.count() == 1) {
                frame.errorMessage?.add(
                    "There is no ${
                        diffString.toString().replace("[", "").replace("]", "")
                    } algorithm for image ${frame.frame.timestamp}"
                )
            } else {
                frame.errorMessage?.add(
                    "There are no ${
                        diffString.toString().replace("[", "").replace("]", "")
                    } algorithms for image ${frame.frame.timestamp}"
                )
            }
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
                landmarks[longName]?.add(LandmarkFile(layers.last(), Path(it.path), extractUIDs(it.path)))
            } catch (e: Exception) {
                errorOutputs.add(imageName)
            }
        }
        if (errorOutputs.isNotEmpty()) {
            createAlert("$errorOutputs are incorrect names of files. They can't be converted to Long")
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
            if (errorImages.count() == 1) {
                createAlert(
                    "Image ${
                        errorImages.toString().replace("[", "").replace("]", "")
                    } is skipped, because the file name cannot be converted to long."
                )
            } else {
                createAlert(
                    "Images ${
                        errorImages.toString().replace("[", "").replace("]", "")
                    } are skipped, because the file names cannot be converted to long."
                )
            }
        }
    }

    fun parseDirectory(path: String): ImporterProject? {
        val directory = File(path)
        val images = FXCollections.observableHashMap<Long, String>()
        val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
        val frames = FXCollections.observableArrayList<ImporterProjectFrame>()
        val layers = FXCollections.observableArrayList<ProjectLayer>()
        val errorFolders = mutableListOf<String>()
        var isImagesExist = false

        for (folder in directory.listFiles()) {
            if (folder.name != IMAGE_DIRECTORY_NAME) {
                parseOutputs(folder, layers, landmarks, errorFolders)
            } else {
                isImagesExist = true
                parseImages(folder, images)
            }
        }

        if (!isImagesExist) {
            createAlert("The images folder is missing in the directory")
            return null
        }

        if (errorFolders.isNotEmpty()) {
            if (errorFolders.count() == 1) {
                createAlert(
                    "The directory ${
                        errorFolders.toString().replace("[", "").replace("]", "")
                    } is skipped, because names of folders don't contain correct kind name."
                )
            } else {
                createAlert(
                    "The directories ${
                        errorFolders.toString().replace("[", "").replace("]", "")
                    } are skipped, because names of folders don't contain correct kind name."
                )
            }
        }

        landmarks.forEach { (name, outputs) ->
            frames.add(ImporterProjectFrame(ProjectFrame(name, Path(images[name]!!), outputs)))
        }

        var hasAnyErrors = false
        frames.forEach {
            incorrectExtensionError(it)
            noSomeAlgorithmError(it, layers)
            if (it.isImageErrored){
                hasAnyErrors = true
            }
        }
        return ImporterProject(Path(path), frames, layers, hasAnyErrors )
    }

    fun createTreeWithFiles(project: ImporterProject, tree: TreeItem<FileInTree>): TreeItem<FileInTree> {
        tree.value = FileInTree(FileInfo())
        project.frames.forEach {
            val imageNode: TreeItem<FileInTree> = if (it.isImageErrored) {
                TreeItem(
                    FileInTree(
                        FileInfo(it.frame.timestamp.toString()),
                    )
                ).apply {
                    this.value.file.error.add(it.errorMessage.toString())
                }
            } else {
                TreeItem(FileInTree(FileInfo(it.frame.timestamp.toString())))
            }
            imageNode.children.addAll(it.frame.landmarkFiles.map { landmark ->
                val layer = landmark.projectLayer
                val fileName = layer.name + "_" + layer.kind.toString().lowercase()
                TreeItem(FileInTree(FileInfo(fileName, isLeaf = true)))
            })
            tree.children.add(imageNode)
        }
        return tree
    }
}