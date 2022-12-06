package solve.project.model

import java.nio.file.Path

data class ProjectFrame(
    val timestamp: Long,
    val imagePath: Path,
    val landmarkFiles: List<LandmarkFile>,
    var errorMsg: MutableList<String> = mutableListOf(),
    var errorType: MutableList<ErrorType> = mutableListOf()
)