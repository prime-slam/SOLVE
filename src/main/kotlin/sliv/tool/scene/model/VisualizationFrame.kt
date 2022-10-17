package sliv.tool.scene.model

import javafx.scene.image.Image

class VisualizationFrame(val image: Image, val timestamp: Long, val landmarks: Map<Layer, List<Landmark>>)