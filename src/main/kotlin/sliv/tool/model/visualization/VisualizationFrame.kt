package sliv.tool.model.visualization

import javafx.scene.image.Image
import java.time.LocalDateTime

class VisualizationFrame(val image: Image, val timestamp: LocalDateTime, val landmarks: Map<Layer, List<Landmark>>) {
}