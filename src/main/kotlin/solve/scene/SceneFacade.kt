package solve.scene

import java.io.FileInputStream
import javafx.scene.image.Image
import solve.parsers.factories.*
import solve.parsers.lines.CSVLinesParser
import solve.parsers.planes.ImagePlanesParser
import solve.parsers.points.CSVPointsParser
import solve.project.model.*
import solve.scene.controller.SceneController
import solve.scene.model.*

// Interaction interface of the scene for main controller
// Should be recreated if new project was imported
class SceneFacade(private val controller: SceneController) {
    private val visualizationLayers = HashMap<String, LayerSettings>()

    // Display new frames with landmarks
    fun visualize(layers: List<ProjectLayer>, frames: List<ProjectFrame>) {
        layers.forEach { projectLayer ->
            if (!visualizationLayers.contains(projectLayer.name)) { //don't rewrite existing settings
                visualizationLayers[projectLayer.name] = projectLayer.toVisualizationLayer()
            }
            visualizationLayers[projectLayer.name]?.clearSelectionAndHoverState()
        }
        val visualizationFrames = frames.map { projectFrame -> projectFrame.toVisualizationFrame() }
        val scene = Scene(visualizationFrames, visualizationLayers.values.toList())
        controller.scene.value = scene
    }

    private fun ProjectLayer.toVisualizationLayer(): LayerSettings {
        return when (kind) {
            LayerKind.Keypoint -> LayerSettings.PointLayerSettings(this.name)
            LayerKind.Line -> LayerSettings.LineLayerSettings(this.name)
            LayerKind.Plane -> LayerSettings.PlaneLayerSettings(this.name)
        }
    }

    private fun ProjectFrame.toVisualizationFrame(): VisualizationFrame {
        val getImage = { Image(FileInputStream(imagePath.toFile())) }

        val getLandmarks = {
            val landmarks = HashMap<LayerSettings, List<Landmark>>()
            landmarkFiles.forEach { file ->
                val visualizationLayer = visualizationLayers[file.projectLayer.name]
                    ?: throw IllegalStateException("No visualization layer is created for ${file.projectLayer.name}")
                landmarks[visualizationLayer] = createLandmarks(file, visualizationLayer)
            }
            landmarks.toMap()
        }

        return VisualizationFrame(this.timestamp, getImage, getLandmarks)
    }

    private fun createLandmarks(file: LandmarkFile, layerSettings: LayerSettings): List<Landmark> {
        return when (file.projectLayer.kind) {
            LayerKind.Keypoint -> CSVPointsParser.parse(file.path.toString()).map { point ->
                PointFactory.buildLandmark(point, layerSettings as LayerSettings.PointLayerSettings)
            }

            LayerKind.Line -> CSVLinesParser.parse(file.path.toString()).map { line ->
                LineFactory.buildLandmark(line, layerSettings as LayerSettings.LineLayerSettings)
            }

            LayerKind.Plane -> ImagePlanesParser.parse(file.path.toString()).map { plane ->
                PlaneFactory.buildLandmark(plane, layerSettings as LayerSettings.PlaneLayerSettings)
            }
        }
    }
}