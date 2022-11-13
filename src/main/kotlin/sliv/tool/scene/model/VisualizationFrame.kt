package sliv.tool.scene.model

import javafx.scene.image.Image

//TODO: null Image have to be removed after data virtualization will be done
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