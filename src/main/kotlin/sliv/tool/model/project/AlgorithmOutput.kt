package sliv.tool.model.project

import java.nio.file.Path

data class AlgorithmOutput(val kind: LayerKind, val path: Path, val containedLandmarks: List<Long>) {
}