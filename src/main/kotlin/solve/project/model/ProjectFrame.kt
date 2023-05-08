package solve.project.model

import java.nio.file.Path

data class ProjectFrame(val timestamp: Long, val imagePath: Path, val landmarkFiles: List<LandmarkFile>) {
    val uids: List<Long> by lazy { landmarkFiles.map { it.containedLandmarksId }.flatten() }
}
