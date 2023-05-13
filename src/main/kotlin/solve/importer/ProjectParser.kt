package solve.importer

import javafx.scene.control.TreeItem
import solve.importer.model.FileInTree
import solve.importer.model.FileInfo
import solve.importer.model.FrameAfterPartialParsing
import solve.importer.model.ImageAfterPartialParsing
import solve.importer.model.OutputAfterPartialParsing
import solve.importer.model.ProjectAfterPartialParsing
import solve.project.model.LayerKind
import solve.utils.createAlertForError
import solve.utils.toStringWithoutBrackets
import java.io.File

object ProjectParser {
    private const val IMAGE_DIRECTORY_NAME = "images"

    private const val PNG_EXTENSION = "png"
    private const val JPG_EXTENSION = "jpg"
    private val possibleExtensions = setOf(PNG_EXTENSION, JPG_EXTENSION)

    private fun selectKind(kindString: String): LayerKind? {
        return when (kindString) {
            "keypoint" -> LayerKind.Keypoint
            "plane" -> LayerKind.Plane
            "line" -> LayerKind.Line
            else -> null
        }
    }

    private fun noSomeAlgorithmError(
        frames: ArrayList<FrameAfterPartialParsing>,
        layers: MutableList<String>
    ): Boolean {
        var hasAnyErrors = false
        frames.forEach { frame ->
            val diff = layers.minus(frame.outputs.map { output -> output.algorithmName }.toSet())
            if (diff.isNotEmpty()) {
                val missingAlgorithms = diff.toStringWithoutBrackets()
                frame.image.errors.add(
                    "There ${if (diff.count() == 1) "is" else "are"} no $missingAlgorithms for " +
                            "image ${frame.image.name}"
                )
                hasAnyErrors = true
            }
        }
        return hasAnyErrors
    }

    private fun incorrectExtensionError(image: File, errors: MutableList<String>) {
        if (!possibleExtensions.contains(image.extension)) {
            errors.add("The image has an incorrect extension")
        }
    }

    private fun partialParseOutputs(
        folder: File,
        outputs: ArrayList<OutputAfterPartialParsing>,
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

            if (imageName.toLongOrNull() != null) {
                outputs.add(
                    OutputAfterPartialParsing(
                        imageName,
                        it.invariantSeparatorsPath,
                        directoryName,
                        kind,
                        mutableListOf()
                    )
                )
                if (!layers.contains(directoryName)) {
                    layers.add(directoryName)
                }
            } else {
                errorOutputs.add(imageName)
            }
        }

        if (errorOutputs.isNotEmpty()) {
            val errors = errorOutputs.toStringWithoutBrackets()
            val countErrors = errorOutputs.count()
            createAlertForError(
                "$errors ${if (countErrors == 1) "is" else "are"} incorrect " +
                        "${if (countErrors == 1) "name for file" else "names for files"} with markup. " +
                        "File name must support conversion to Long."
            )
        }
    }

    private fun partialParseImages(folder: File, images: ArrayList<ImageAfterPartialParsing>) {
        val errorImages = mutableListOf<String>()
        folder.listFiles()?.forEach {
            val imageName = it.nameWithoutExtension

            if (imageName.toLongOrNull() != null) {
                images.add(
                    ImageAfterPartialParsing(imageName, it.invariantSeparatorsPath, mutableListOf()).apply {
                        incorrectExtensionError(it, errors)
                    }
                )
            } else {
                errorImages.add(imageName)
            }
        }

        fun alertErrorImages() {
            if (errorImages.isNotEmpty()) {
                createAlertForError(
                    "Image ${
                        if (errorImages.count() == 1) {
                            "${errorImages.toStringWithoutBrackets()} is"
                        } else {
                            "${errorImages.first()} and ${errorImages.count() - 1} others are"
                        }
                    } " +
                            "because the file name can't be converted to long"
                )
            }
        }

        if (errorImages.isNotEmpty()) alertErrorImages()
    }

    fun partialParseDirectory(path: String): ProjectAfterPartialParsing? {
        val directory = File(path)
        val images = ArrayList<ImageAfterPartialParsing>()
        val outputs = ArrayList<OutputAfterPartialParsing>()
        val algorithms = mutableListOf<String>()
        val frames = ArrayList<FrameAfterPartialParsing>()
        val layers = mutableListOf<String>()
        var hasAnyErrors = false

        val errorFolders = mutableListOf<String>()
        var isImagesExist = false

        fun alertErrorFolderMessage() {
            createAlertForError(
                "The directory ${
                    if (errorFolders.count() == 1) {
                        "${errorFolders.toStringWithoutBrackets()} is"
                    } else {
                        "${errorFolders.first()} and ${errorFolders.count() - 1} others are"
                    }
                } skipped, " +
                        "because names of folders don't contain correct kind name. " +
                        "The correct directory name should look like: name_kind"
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

        if (!isImagesExist) {
            createAlertForError("The images folder is missing in the directory")
            return null
        }

        images.map { img ->
            hasAnyErrors = hasAnyErrors || img.errors.isNotEmpty()

            frames.add(FrameAfterPartialParsing(img, outputs.filter { img.name == it.name }))
        }

        hasAnyErrors = hasAnyErrors || noSomeAlgorithmError(frames, layers)

        frames.sortWith(compareBy { it.image.name.toLong() })

        if (errorFolders.isNotEmpty()) {
            alertErrorFolderMessage()
        }

        return ProjectAfterPartialParsing(path, frames, hasAnyErrors)
    }

    fun createTreeWithFiles(
        project: ProjectAfterPartialParsing,
        tree: TreeItem<FileInTree>
    ): TreeItem<FileInTree> {
        tree.value = FileInTree(FileInfo())
        project.projectFrames.forEach {
            val image = it.image
            val imageNode: TreeItem<FileInTree> = TreeItem(FileInTree(FileInfo(image.name, false, image.errors)))
            imageNode.children.addAll(
                it.outputs.map { output ->
                    TreeItem(FileInTree(FileInfo(output.algorithmName, true, output.errors)))
                }
            )
            tree.children.add(imageNode)
        }
        return tree
    }
}
