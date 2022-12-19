package solve.scene.model

import javafx.scene.image.Image

class VisualizationFrame(
    val timestamp: Long,
    private val getImage: () -> Image,
    private val getLandmarks: () -> Map<LayerSettings, List<Landmark>>
) {
    val image: Image
        get() = getImage()

    val landmarks: Map<LayerSettings, List<Landmark>>
        get() = getLandmarks()
}