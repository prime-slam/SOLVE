package solve.importer.model

import solve.project.model.ProjectLayer
import java.nio.file.Path

data class ImporterProject(
    val currentDirectory: Path,
    val frames: List<ImporterProjectFrame>,
    val layers: List<ProjectLayer>,
    val hasAnyErrors: Boolean = false
)