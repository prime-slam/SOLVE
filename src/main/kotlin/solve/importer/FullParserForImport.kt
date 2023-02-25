package solve.importer

import javafx.collections.FXCollections
import solve.importer.model.ProjectAfterPartialParsing
import solve.parsers.planes.ImagePlanesParser
import solve.project.model.LandmarkFile
import solve.project.model.Project
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import kotlin.io.path.Path

object FullParserForImport {
    fun fullParseDirectory(
        project: ProjectAfterPartialParsing
    ): Project {
        val landmarks = FXCollections.observableHashMap<Long, MutableList<LandmarkFile>>()
        val frames = FXCollections.observableArrayList<ProjectFrame>()
        val layers = FXCollections.observableArrayList<ProjectLayer>()

        project.projectFrames.first().outputs.forEach {
            layers.add(ProjectLayer(it.kind, it.algorithmName))
        }

        project.projectFrames.forEach {
            val longName = it.image.name.toLong()
            landmarks[longName] = mutableListOf()
            it.outputs.map { output ->
                val currentLayer = ProjectLayer(output.kind, output.algorithmName)
                if (!layers.contains(currentLayer)) {
                    layers.add((currentLayer))
                }
                landmarks[longName]?.add(LandmarkFile(currentLayer, Path(output.path), ImagePlanesParser.extractUIDs(output.path)))
            }
            frames.add(landmarks[longName]?.toList()
                ?.let { landmark -> ProjectFrame(longName, Path(it.image.path), landmark) })
        }
        return Project(Path(project.currentDirectory), frames, layers)
    }
}