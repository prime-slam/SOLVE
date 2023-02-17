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

    private fun noSomeAlgorithmError(
        frames: ObservableList<FrameAfterPartialParsing>,
        layers: MutableList<String>
    ): Boolean {
        var hasAnyErrors = false
        frames.forEach { frame ->
            val diff = layers.minus(frame.outputs.map { output -> output.algorithmName }.toSet())
            if (diff.isNotEmpty()) {
                val missingAlgorithms = diff.toMutableList().toStringWithoutBrackets()
                frame.image.errors.add("There ${if (diff.count() == 1) "is" else "are"} no $missingAlgorithms for image ${frame.image.name}")
                hasAnyErrors = true
            }
        }
        return hasAnyErrors
    }

    private fun partialParseOutputs(
        folder: File,
        outputs: ObservableList<OutputAfterPartialParsing>,
        errorFolders: MutableList<String>,
        algorithmsList: MutableList<String>,
        layers: MutableList<String>
    ) {
        val directoryName = folder.name
        algorithmsList.add(directoryName)
        val indexOfSeparator = directoryName.lastIndexOf("_")
        if (indexOfSeparator == -1) {
            errorFolders.add(directoryName)
            return
        }
        val kindString = directoryName.substring(indexOfSeparator + 1)
        val kind = selectKind(kindString)
        if (kind == null) {
            errorFolders.add(directoryName)
            return
        }
        val errorOutputs = mutableListOf<String>()
        folder.listFiles()?.forEach {
            val imageName = it.nameWithoutExtension
            try {
                imageName.toLong()
                outputs.add(OutputAfterPartialParsing(imageName, it.absolutePath, directoryName, kind))
                if (!layers.contains(directoryName)) {
                    layers.add(directoryName)
                }

            } catch (e: Exception) {
                errorOutputs.add(imageName)
            }
        }
        if (errorOutputs.isNotEmpty()) {
            val ownerWindow = importer.root.scene.window
            createAlertForError(
                "$errorOutputs are incorrect names of files. They can't be converted to Long",
                ownerWindow
            )
        }
    }

    private fun partialParseImages(folder: File, images: ObservableList<ImageAfterPartialParsing>) {
        val errorImages = mutableListOf<String>()
        folder.listFiles()?.forEach {
            val imageName = it.nameWithoutExtension
            try {
                imageName.toLong()
                if (!possibleExtensions.contains(it.extension)) {
                    images.add(
                        ImageAfterPartialParsing(
                            imageName,
                            path = it.absolutePath
                        ).apply { errors.add("The image has an incorrect extension") })
                } else {
                    images.add(ImageAfterPartialParsing(imageName, path = it.absolutePath))
                }
            } catch (e: Exception) {
                errorImages.add(imageName)
            }
        }

        fun alertErrorImages() {
            if (errorImages.isNotEmpty()) {
                val ownerWindow = importer.root.scene.window
                createAlertForError(
                    "Image ${
                        if (errorImages.count() == 1) "${errorImages.toStringWithoutBrackets()} is"
                        else "${errorImages.first()} and ${errorImages.count() - 1} others are"
                    } " +
                            "because the file name can't be converted to long", ownerWindow
                )
            }
        }

        if (errorImages.isNotEmpty()) alertErrorImages()
    }

    fun partialParseDirectory(path: String): ProjectAfterPartialParsing? {
        val directory = File(path)
        val images = FXCollections.observableArrayList<ImageAfterPartialParsing>()
        val outputs = FXCollections.observableArrayList<OutputAfterPartialParsing>()
        val algorithms = mutableListOf<String>()
        val frames = FXCollections.observableArrayList<FrameAfterPartialParsing>()
        val layers = mutableListOf<String>()
        var hasAnyErrors = false

        val errorFolders = mutableListOf<String>()
        var isImagesExist = false

        fun alertErrorFolderMessage() {
            val ownerWindow = importer.root.scene.window
            createAlertForError(
                "The directory ${
                    if (errorFolders.count() == 1) "${errorFolders.toStringWithoutBrackets()} is" else
                        "${errorFolders.first()} and ${errorFolders.count() - 1} others are"
                } skipped, " +
                        "because names of folders don't contain correct kind name. " +
                        "The correct directory name should look like: name_kind", ownerWindow
            )
        }

        for (folder in directory.listFiles()) {
            if (folder.name != IMAGE_DIRECTORY_NAME) {
                partialParseOutputs(folder, outputs, errorFolders, algorithms, layers)
            } else {
                isImagesExist = true
                partialParseImages(folder, images)
            }
        }
        images.forEach { img ->
            if (img.errors.isNotEmpty()) {
                hasAnyErrors = true
            }
            frames.add(FrameAfterPartialParsing(img, outputs.filter { img.name == it.name }))
        }

        if (noSomeAlgorithmError(frames, layers)) hasAnyErrors = true

        frames.sortWith(compareBy { it.image.name.toLong() })

        if (!isImagesExist) {
            val ownerWindow = importer.root.scene.window
            createAlertForError("The images folder is missing in the directory", ownerWindow)
            return null
        }

        if (errorFolders.isNotEmpty()) {
            alertErrorFolderMessage()
        }

        return ProjectAfterPartialParsing(path, frames, hasAnyErrors)
    }

    fun fullParseDirectory(
        project: ProjectAfterPartialParsing
    ): Project {
        val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
        val frames = FXCollections.observableArrayList<ProjectFrame>()
        val layers = FXCollections.observableArrayList<ProjectLayer>()

        project.value.first().outputs.forEach {
            layers.add(ProjectLayer(it.kind, it.algorithmName))
        }

        project.value.forEach {
            val longName = it.image.name.toLong()
            landmarks[longName] = mutableListOf()
            it.outputs.forEach { output ->
                val currentLayer = ProjectLayer(output.kind, output.algorithmName)
                if (!layers.contains(currentLayer)) {
                    layers.add((currentLayer))
                }
                landmarks[longName]?.add(LandmarkFile(currentLayer, Path(output.path), extractUIDs(output.path)))
            }
            frames.add(
                landmarks[longName]?.toList()
                    ?.let { landmark -> ProjectFrame(longName, Path(it.image.path), landmark) })
        }
        return Project(Path(project.currentDirectory), frames, layers)
    }

    fun createTreeWithFiles(
        project: ProjectAfterPartialParsing,
        tree: TreeItem<FileInTree>
    ): TreeItem<FileInTree> {
        tree.value = FileInTree(FileInfo())
        project.value.forEach {
            val image = it.image
            val imageNode: TreeItem<FileInTree> = TreeItem(FileInTree(FileInfo(image.name, false, image.errors)))
            imageNode.children.addAll(it.outputs.map { output ->
                TreeItem(FileInTree(FileInfo(output.algorithmName, true, output.errors)))
            })
            tree.children.add(imageNode)
        }
        return tree
    }
}