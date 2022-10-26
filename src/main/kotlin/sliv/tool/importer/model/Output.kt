package sliv.tool.importer.model

import sliv.tool.project.model.LayerKind
import java.nio.file.Path

class Output(name: String, directoryPath: Path, val type: LayerKind) : ProjectFile(name, directoryPath) {
}