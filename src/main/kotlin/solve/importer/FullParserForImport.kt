package solve.importer

import solve.importer.model.ProjectAfterPartialParsing
import solve.parsers.lines.CSVLinesParser
import solve.parsers.planes.ImagePlanesParser
import solve.project.model.LandmarkFile
import solve.project.model.LayerKind
import solve.project.model.Project
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import kotlin.io.path.Path

object FullParserForImport {
    fun fullParseDirectory(
        project: ProjectAfterPartialParsing
    ): Project {
        val landmarks = HashMap<Long, MutableList<LandmarkFile>>()
        val frames = ArrayList<ProjectFrame>()
        val layers = ArrayList<ProjectLayer>()

        project.projectFrames.first().outputs.forEach {
            layers.add(ProjectLayer(it.kind, it.algorithmName, project.currentDirectory))
        }

        project.projectFrames.forEach {
            val longName = it.image.name.toLong()
            landmarks[longName] = mutableListOf()
            it.outputs.map { output ->
                val currentLayer = ProjectLayer(output.kind, output.algorithmName, project.currentDirectory)
                if (!layers.contains(currentLayer)) {
                    layers.add((currentLayer))
                }
                val uids = when (output.kind) {
                    LayerKind.Keypoint -> CSVLinesParser.extractUIDs(output.path)
                    LayerKind.Line -> CSVLinesParser.extractUIDs(output.path)
                    LayerKind.Plane -> ImagePlanesParser.extractUIDs(output.path)
                }
                landmarks[longName]?.add(
                    LandmarkFile(currentLayer, Path(output.path), uids)
                )
            }
            landmarks[longName]?.toList()
                ?.let { landmark -> ProjectFrame(longName, Path(it.image.path), landmark) }
                ?.let { it1 -> frames.add(it1) }
        }
        return Project(Path(project.currentDirectory), frames, layers)
    }
}
