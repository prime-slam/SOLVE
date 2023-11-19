package solve.scene.model

import java.nio.file.Path

/**
 * Frame data object.
 */
data class VisualizationFrame(
    val timestamp: Long,
    val imagePath: Path,
    val layers: List<Layer>
)
