package sliv.tool.scene

import java.io.FileInputStream
import javafx.scene.image.Image
import sliv.tool.parsers.factories.*
import sliv.tool.parsers.lines.CSVLinesParser
import sliv.tool.parsers.planes.ImagePlanesParser
import sliv.tool.parsers.points.CSVPointsParser
import sliv.tool.project.model.*
import sliv.tool.scene.controller.SceneController
import sliv.tool.scene.model.*

// Interaction interface of the scene for main controller
// Should be recreated if new project was imported
class SceneFacade(private val controller: SceneController) {
    private val visualizationLayers = HashMap<String, Layer>()

    // Display new frames with landmarks
    fun visualize(layers: List<ProjectLayer>, frames: List<ProjectFrame>) {
        layers.forEach { projectLayer ->
            if (!visualizationLayers.contains(projectLayer.name)) { //don't rewrite existing settings
                visualizationLayers[projectLayer.name] = projectLayer.toVisualizationLayer()
            }
        }
        val visualizationFrames = frames.map { projectFrame -> projectFrame.toVisualizationFrame() }
        val scene = Scene(visualizationFrames, visualizationLayers.values.toList())
        controller.scene.value = scene
    }

    private fun ProjectLayer.toVisualizationLayer(): Layer {
        return when (kind) {
            LayerKind.Keypoint -> Layer.PointLayer(this.name)
            LayerKind.Line -> Layer.LineLayer(this.name)
            LayerKind.Plane -> Layer.PlaneLayer(this.name)
        }
    }

    private fun ProjectFrame.toVisualizationFrame(): VisualizationFrame {
        val getImage = { Image(FileInputStream(imagePath.toFile())) }

        val getLandmarks = {
            val landmarks = HashMap<Layer, List<Landmark>>()
            landmarkFiles.forEach { file ->
                val visualizationLayer = visualizationLayers[file.projectLayer.name]
                    ?: throw IllegalStateException("No visualization layer is created for ${file.projectLayer.name}")
                landmarks[visualizationLayer] = createLandmarks(file, visualizationLayer)
            }
            landmarks.toMap()
        }

        return VisualizationFrame(this.timestamp, getImage, getLandmarks)
    }

    private fun createLandmarks(file: LandmarkFile, layer: Layer): List<Landmark> {
        return when (file.projectLayer.kind) {
            LayerKind.Keypoint -> CSVPointsParser.parse(file.path.toString()).map { point ->
                PointFactory.buildLandmark(point, layer as Layer.PointLayer)
            }

            LayerKind.Line -> CSVLinesParser.parse(file.path.toString()).map { line ->
                LineFactory.buildLandmark(line, layer as Layer.LineLayer)
            }

            LayerKind.Plane -> ImagePlanesParser.parse(file.path.toString()).map { plane ->
                PlaneFactory.buildLandmark(plane, layer as Layer.PlaneLayer)
            }
        }
    }
}