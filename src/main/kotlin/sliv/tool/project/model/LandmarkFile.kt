package sliv.tool.project.model

import java.nio.file.Path

data class LandmarkFile(val projectLayer: ProjectLayer, val path: Path, val containedLandmarks: List<Long>)