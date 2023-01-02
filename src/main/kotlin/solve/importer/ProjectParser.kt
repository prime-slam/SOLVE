package solve.importer

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.control.Alert
import javafx.scene.control.TreeItem
import javafx.stage.Modality
import solve.importer.model.ColumnData
import solve.importer.model.FileInTree
import solve.importer.view.ImporterView
import solve.parsers.planes.ImagePlanesParser.extractUIDs
import solve.project.model.*
import tornadofx.FX.Companion.find
import tornadofx.alert
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.extension

object ProjectParser {
    private const val IMAGE_DIRECTORY_NAME = "images"

    private val importer = find<ImporterView>()


    private fun selectKind(kindString: String): LayerKind? {
        return when (kindString) {
            "keypoint" -> LayerKind.Keypoint
            "plane" -> LayerKind.Plane
            "line" -> LayerKind.Line
            else -> null
        }
    }

    fun createAlert(content: String): Alert {
        return Alert(Alert.AlertType.ERROR, content).apply {
            headerText = ""
            initModality(Modality.APPLICATION_MODAL)
            initOwner(importer.root.scene.window)
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
            alert(
                Alert.AlertType.WARNING,
                "",
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
            val alert = createAlert("Image(s) $errorImages is (are) skipped, because the file name cannot be converted to long.")
            alert.show()
        }
    }

    fun parseDirectory(path: String): Project? {
        val directory = File(path)
        val images = FXCollections.observableHashMap<Long, String>()
        val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
        val frames = FXCollections.observableArrayList<ProjectFrame>()
        val layers = FXCollections.observableArrayList<ProjectLayer>()
        val errorFolders = mutableListOf<String>()
        val possibleExtensions = setOf("png", "jpg", "jpeg", "gif")

        var countImages = 0

        for (folder in directory.listFiles()!!) {
            if (folder.name != IMAGE_DIRECTORY_NAME) {
                parseOutputs(folder, layers, landmarks, errorFolders)
            } else {
                countImages += 1
                parseImages(folder, images)
            }
        }
        if (countImages != 1) {
            val alert = createAlert("The images folder is missing in the directory")
            alert.show()
            return null
        }

        if (errorFolders.isNotEmpty()) {
            val alert = createAlert(
                "The directory(ies) ${
                    errorFolders.toString().replace("[", "").replace("]", "")
                } is/are skipped, because names of folders don't contain correct kind name."
            )
            alert.show()

        }
        val errorName = mutableListOf<String>()
        landmarks.forEach { (name, outputs) ->
            try {
                val longName = name.toLong()
                frames.add(ProjectFrame(longName, Path(images[name]!!), outputs))
            } catch (e: Exception) {
                errorName.add(name.toString())
                val alert = createAlert(
                    "Image(s) ${
                        name.toString().replace("[", "").replace("]", "")
                    } are skipped, because the file name cannot be converted to long."
                )
                alert.show()

            }
        }
        frames.forEach {
            if (!possibleExtensions.contains(it.imagePath.extension)) {
                it.isImageErrored = true
                it.errorMessage?.add("The image has an incorrect extension")
            }
            val diff = layers.minus(it.landmarkFiles.map { landmark ->
                landmark.projectLayer
            }.toSet())
            diff.map {
                it.name + "_" + it.kind

            }

            if (diff.isNotEmpty()) {
                it.isImageErrored = true
                it.errorMessage?.add("There is no $diff algorithms for image ${it.timestamp}")

            }
        }
        return Project(Path(path), frames, layers)
    }

    fun createTreeWithFiles(project: Project, tree: TreeItem<FileInTree>): TreeItem<FileInTree> {

        tree.value = FileInTree(ColumnData())
        project.frames.forEach {
            val imageNode: TreeItem<FileInTree>
            if (it.isImageErrored) {
                imageNode = TreeItem(
                    FileInTree(
                        ColumnData(it.timestamp.toString()),
                        error = ColumnData(it.errorMessage.toString())
                    )
                ).apply {
                    this.value.name.error.add(it.errorMessage.toString())
                }
            } else {
                imageNode = TreeItem(FileInTree(ColumnData(it.timestamp.toString()), isLeaf = false))
            }
            imageNode.children.addAll(it.landmarkFiles.map { landmark ->
                val layer = landmark.projectLayer
                val fileName = layer.name + "_" + layer.kind.toString().lowercase()
                TreeItem(FileInTree(ColumnData(fileName, isLeaf = true), isLeaf = true))
            })
            tree.children.add(imageNode)
        }
        return tree
    }
}