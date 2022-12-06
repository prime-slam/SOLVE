package solve.scene.model

import javafx.scene.image.Image

class VisualizationFrame(
    val timestamp: Long,
    private val getImage: () -> Image,
    private val getLandmarks: () -> Map<Layer, List<Landmark>>
) {
    val image: Image
        get() = getImage()

    val landmarks: Map<Layer, List<Landmark>>
        get() = getLandmarks()
}