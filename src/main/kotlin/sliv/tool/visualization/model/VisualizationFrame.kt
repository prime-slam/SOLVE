package sliv.tool.visualization.model

import javafx.scene.image.Image
import java.time.LocalDateTime

class VisualizationFrame(val image: Image, val timestamp: LocalDateTime, val landmarks: Map<Layer, List<Landmark>>)