package solve.importer

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.control.TreeItem
import solve.importer.model.*
import solve.importer.view.ImporterView
import solve.parsers.planes.ImagePlanesParser.extractUIDs
import solve.project.model.*
import solve.utils.createAlertForError
import solve.utils.toStringWithoutBrackets
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

    private val ownerWindow = importer.root.scene.window

    private fun selectKind(kindString: String): LayerKind? {
        return when (kindString) {
            "keypoint" -> LayerKind.Keypoint
            "plane" -> LayerKind.Plane
            "line" -> LayerKind.Line
            else -> null
        }
    }

    private fun incorrectExtensionError(frame: ImporterProjectFrame) {
        if (!possibleExtensions.contains(frame.importerFrame.imagePath.extension)) {
            frame.isImageErrored = true
            frame.errorMessage.add("The image has an incorrect extension")
        }
    }

    private fun noSomeAlgorithmError(frame: ImporterProjectFrame, layers: ObservableList<ProjectLayer>) {
        val diff = layers.minus(frame.importerFrame.landmarkFiles.map { landmark ->
            landmark.projectLayer
        }.toSet())
        val diffString = diff.map { layer ->
            layer.name
        }

        fun addNoSomeAlgorithmErrorMessage() {
            val missingAlgorithms = diffString.toMutableList().toStringWithoutBrackets()
            val imageTimestamp = frame.importerFrame.timestamp
            if (diff.count() == 1) {
                frame.errorMessage.add("There is no $missingAlgorithms algorithm for image $imageTimestamp")
            } else {
                frame.errorMessage.add("There are no $missingAlgorithms algorithms for image $imageTimestamp")
            }
        }

        if (diff.isNotEmpty()) {
            frame.isImageErrored = true
            addNoSomeAlgorithmErrorMessage()
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
        val kindString = directoryName.substring(indexOfSeparator + 1)

        val kind = selectKind(kindString)
        if (kind != null) {
            layers.add(ProjectLayer(kind, directoryName))
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
            createAlertForError(
                "$errorOutputs are incorrect names of files. They can't be converted to Long",
                importer.root.scene.window
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

        fun alertErrorImages() {
            if (errorImages.isNotEmpty()) {
                if (errorImages.count() == 1) {
                    createAlertForError(
                        "Image ${errorImages.toStringWithoutBrackets()} is skipped, " +
                                "because the file name can't be converted to long", ownerWindow
                    )
                } else {
                    createAlertForError(
                        "Image ${errorImages.first()} and ${errorImages.count() - 1} others are skipped, " +
                                "because the file name can't be converted to long", ownerWindow
                    )
                }
            }
        }

        if (errorImages.isNotEmpty()) alertErrorImages()
    }

    fun parseDirectory(path: String): ImporterProject? {
        val directory = File(path)
        val images = FXCollections.observableHashMap<Long, String>()
        val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
        val frames = FXCollections.observableArrayList<ImporterProjectFrame>()
        val layers = FXCollections.observableArrayList<ProjectLayer>()
        val errorFolders = mutableListOf<String>()
        var isImagesExist = false

        fun alertErrorFolderMessage() {
            if (errorFolders.count() == 1) {
                createAlertForError(
                    "The directory ${errorFolders.toStringWithoutBrackets()} is skipped, " +
                            "because names of folders don't contain correct kind name. " +
                            "The correct directory name should look like: name_kind", ownerWindow
                )
            } else {
                createAlertForError(
                    "The directory ${errorFolders.first()}  and ${errorFolders.count() - 1} others are skipped, " +
                            "because names of folders don't contain correct kind name. " +
                            "The correct directory name should look like: name_kind", ownerWindow
                )
            }
        }

        for (folder in directory.listFiles()) {
            if (folder.name != IMAGE_DIRECTORY_NAME) {
                parseOutputs(folder, layers, landmarks, errorFolders)
            } else {
                isImagesExist = true
                parseImages(folder, images)
            }
        }

        if (!isImagesExist) {
            createAlertForError("The images folder is missing in the directory", ownerWindow)
            return null
        }

        if (errorFolders.isNotEmpty()) {
            alertErrorFolderMessage()
        }

        landmarks.forEach { (name, outputs) ->
            frames.add(ImporterProjectFrame(ProjectFrame(name, Path(images[name]!!), outputs)))
        }

        var hasAnyErrors = false
        frames.forEach {
            incorrectExtensionError(it)
            noSomeAlgorithmError(it, layers)
            if (it.isImageErrored) {
                hasAnyErrors = true
            }
        }
        return ImporterProject(Path(path), frames, layers, hasAnyErrors)
    }


    fun createTreeWithFiles(project: ImporterProject, tree: TreeItem<FileInTree>): TreeItem<FileInTree> {
        tree.value = FileInTree(FileInfo())
        project.frames.forEach {
            val imageNode: TreeItem<FileInTree> = if (it.isImageErrored) {
                TreeItem(
                    FileInTree(
                        FileInfo(it.importerFrame.timestamp.toString()),
                    )
                ).apply {
                    this.value.file.errors.add(it.errorMessage.toString())
                }
            } else {
                TreeItem(FileInTree(FileInfo(it.importerFrame.timestamp.toString())))
            }
            imageNode.children.addAll(it.importerFrame.landmarkFiles.map { landmark ->
                val layer = landmark.projectLayer
                val fileName = layer.name
                TreeItem(FileInTree(FileInfo(fileName, isLeaf = true)))
            })
            tree.children.add(imageNode)
        }
        return tree
    }
}