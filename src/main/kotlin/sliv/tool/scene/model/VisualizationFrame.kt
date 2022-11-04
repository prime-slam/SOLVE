package sliv.tool.scene.model

import javafx.scene.image.Image

//TODO: null Image have to be removed after data virtualization will be done
class VisualizationFrame(val image: Image?, val timestamp: Long, val landmarks: Map<Layer, List<Landmark>>)