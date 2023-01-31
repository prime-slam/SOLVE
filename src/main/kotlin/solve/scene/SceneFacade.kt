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
                visualizationLayers[projectLayer.name] = projectLayer.toLayerSettings()
            }
        }
        val layerStates = layers.map { projectLayer -> LayerState(projectLayer.name) }
        val visualizationFrames = frames.map { projectFrame -> projectFrame.toVisualizationFrame(layerStates) }
        val scene = Scene(visualizationFrames, visualizationLayers.values.toList())
        controller.scene.value = scene
    }

    private fun ProjectLayer.toLayerSettings(): LayerSettings {
        return when (kind) {
            LayerKind.Keypoint -> LayerSettings.PointLayerSettings(this.name)
            LayerKind.Line -> LayerSettings.LineLayerSettings(this.name)
            LayerKind.Plane -> LayerSettings.PlaneLayerSettings(this.name)
        }
    }

    private fun ProjectFrame.toVisualizationFrame(layerStates: List<LayerState>): VisualizationFrame {
        val getImage = { Image(FileInputStream(imagePath.toFile())) }

        val layers = landmarkFiles.map { file ->
            val layerSettings = visualizationLayers[file.projectLayer.name]
                ?: throw IllegalStateException("No visualization layer is created for ${file.projectLayer.name}")
            val layerState = layerStates.single { x -> x.name == file.projectLayer.name}
            createLayer(file, layerSettings, layerState)
        }

        return VisualizationFrame(this.timestamp, getImage, layers)
    }

    private fun createLayer(file: LandmarkFile, layerSettings: LayerSettings, layerState: LayerState): Layer {
        return when (file.projectLayer.kind) {
            LayerKind.Keypoint -> Layer.PointLayer(
                file.projectLayer.name,
                layerSettings as LayerSettings.PointLayerSettings
            ) {
                CSVPointsParser.parse(file.path.toString()).map { point ->
                    PointFactory.buildLandmark(point, layerSettings, layerState)
                }
            }
            LayerKind.Line -> Layer.LineLayer(
                file.projectLayer.name,
                layerSettings as LayerSettings.LineLayerSettings
            ) {
                CSVLinesParser.parse(file.path.toString()).map { line ->
                    LineFactory.buildLandmark(line, layerSettings, layerState)
                }
            }
            LayerKind.Plane -> Layer.PlaneLayer(
                file.projectLayer.name,
                layerSettings as LayerSettings.PlaneLayerSettings
            ) {
                ImagePlanesParser.parse(file.path.toString()).map { plane ->
                    PlaneFactory.buildLandmark(plane, layerSettings, layerState)
                }
            }
        }
    }
}