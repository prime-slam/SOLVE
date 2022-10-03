package sliv.tool.view

import javafx.scene.image.Image
import sliv.tool.controller.VisualizationController
import tornadofx.*
import java.io.FileInputStream

//Image(FileInputStream("real-data-set1.png"))

class SceneView : View() {
    private val controller : VisualizationController by inject()

    override val root = vbox {
        scrollpane {
            group {
                imageview(Image(FileInputStream("real-data-set1.png"))) {
                    scaleX = 5.0
                    scaleY = 5.0
                }
                val pointsCount = 10
                val pointsSize = 10.0
                val step = 50
                for (x in 0..pointsCount) {
                    val coordinate = (-x * step).toDouble()
                    rectangle(coordinate, coordinate, pointsSize, pointsSize) { this.fillProperty().bind(controller.landmarksColorProperty) }
                    rectangle(-coordinate, coordinate, pointsSize, pointsSize) { this.fillProperty().bind(controller.landmarksColorProperty) }
                    rectangle(coordinate, -coordinate, pointsSize, pointsSize) { this.fillProperty().bind(controller.landmarksColorProperty) }
                    rectangle(-coordinate, -coordinate, pointsSize, pointsSize) { this.fillProperty().bind(controller.landmarksColorProperty) }
                }
            }
        }
    }
}
