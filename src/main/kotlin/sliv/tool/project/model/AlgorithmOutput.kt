package sliv.tool.project.model

import java.nio.file.Path

data class AlgorithmOutput(val kind: LayerKind, val path: Path, val containedLandmarks: List<Long>) {
}