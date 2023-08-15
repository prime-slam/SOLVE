package solve.project.model

import java.nio.file.Path

data class ProjectFrame(
    val timestamp: Double,
    val imagePath: Path,
    val landmarkFiles: List<LandmarkFile>
)
