package sliv.tool.importer.model

import java.nio.file.Path

class Image(name: String, directoryPath: Path, var outputs: List<Output>) : ProjectFile(name, directoryPath) {

}