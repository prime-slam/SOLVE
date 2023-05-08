package solve.scene.model

import javafx.scene.image.Image

/**
 * Frame data object.
 */
data class VisualizationFrame(
    val timestamp: Long,
    val getImage: () -> Image,
    val layers: List<Layer>
)
