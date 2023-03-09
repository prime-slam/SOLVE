package solve.scene.model

import javafx.scene.image.Image

data class VisualizationFrame(
    val timestamp: Long, val getImage: () -> Image, val layers: List<Layer>
)
