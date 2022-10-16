package sliv.tool.project.model

import java.nio.file.Path
import java.time.LocalDateTime

data class ProjectFrame(val timestamp: LocalDateTime, val imagePath: Path, val outputs: List<LandmarksFile>)