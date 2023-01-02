package solve.project.model

import java.nio.file.Path

data class ProjectFrame(
    val timestamp: Long,
    val imagePath: Path,
    val landmarkFiles: List<LandmarkFile>,
    var isImageErrored: Boolean = false,
    var isOutputErrored: Boolean = false,
    var errorMessage: MutableList<String>? = mutableListOf()
    )