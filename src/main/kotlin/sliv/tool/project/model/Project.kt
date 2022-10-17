package sliv.tool.project.model

import java.nio.file.Path

data class Project(val currentDirectory: Path, val frames: List<ProjectFrame>, val layers: List<ProjectLayer>)