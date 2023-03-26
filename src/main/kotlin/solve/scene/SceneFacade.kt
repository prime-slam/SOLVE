package solve.scene

import java.io.FileInputStream
import javafx.scene.image.Image
import solve.parsers.factories.LineFactory
import solve.parsers.factories.PlaneFactory
import solve.parsers.factories.PointFactory
import solve.parsers.lines.CSVLinesParser
import solve.parsers.planes.ImagePlanesParser
import solve.parsers.points.CSVPointsParser
import solve.project.model.LandmarkFile
import solve.project.model.LayerKind
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import solve.scene.controller.SceneController
import solve.scene.model.ColorManager
import solve.scene.model.Layer
import solve.scene.model.LayerSettings
import solve.scene.model.LayerState
import solve.scene.model.Scene
import solve.scene.model.VisualizationFrame

// Interaction interface of the scene for main controller
// Should be recreated if new project was imported
class SceneFacade(private val controller: SceneController) {
    private val visualizationLayers = HashMap<String, LayerSettings>()

    // Is used to set unique colors for layers where all landmarks have the same color
    private val layersColorManager = ColorManager<String>()

    // Display new frames with landmarks
    fun visualize(layers: List<ProjectLayer>, frames: List<ProjectFrame>, keepSettings: Boolean) {
        val layersSettings = layers.map { projectLayer ->
            visualizationLayers[projectLayer.name] ?: projectLayer.toLayerSettings()
                .also { visualizationLayers[projectLayer.name] = it }
        }
        val layerStates = layers.map { projectLayer -> LayerState(projectLayer.name) }
        val visualizationFrames = frames.map { projectFrame -> projectFrame.toVisualizationFrame(layerStates) }
        val scene = Scene(visualizationFrames, layersSettings)
        controller.setScene(scene, keepSettings)
    }

    private fun ProjectLayer.toLayerSettings(): LayerSettings {
        return when (kind) {
            LayerKind.Keypoint -> LayerSettings.PointLayerSettings(this.name, layersColorManager)
            LayerKind.Line -> LayerSettings.LineLayerSettings(this.name, layersColorManager)
            LayerKind.Plane -> LayerSettings.PlaneLayerSettings(this.name, layersColorManager)
        }
    }

    private fun ProjectFrame.toVisualizationFrame(layerStates: List<LayerState>): VisualizationFrame {
        val getImage = { Image(FileInputStream(imagePath.toFile())) }

        val layers = landmarkFiles.map { file ->
            val layerSettings = visualizationLayers[file.projectLayer.name]
                ?: throw IllegalStateException("No visualization layer is created for ${file.projectLayer.name}")
            val layerState = layerStates.single { x -> x.name == file.projectLayer.name }
            createLayer(file, layerSettings, layerState)
        }

        return VisualizationFrame(this.timestamp, getImage, layers)
    }

    private fun createLayer(file: LandmarkFile, layerSettings: LayerSettings, layerState: LayerState): Layer {
        return when (file.projectLayer.kind) {
            LayerKind.Keypoint -> Layer.PointLayer(
                file.projectLayer.name, layerSettings as LayerSettings.PointLayerSettings
            ) {
                CSVPointsParser.parse(file.path.toString()).map { point ->
                    PointFactory.buildLandmark(point, layerSettings, layerState)
                }
            }

            LayerKind.Line -> Layer.LineLayer(
                file.projectLayer.name, layerSettings as LayerSettings.LineLayerSettings
            ) {
                CSVLinesParser.parse(file.path.toString()).map { line ->
                    LineFactory.buildLandmark(line, layerSettings, layerState)
                }
            }

            LayerKind.Plane -> Layer.PlaneLayer(
                file.projectLayer.name, layerSettings as LayerSettings.PlaneLayerSettings
            ) {
                ImagePlanesParser.parse(file.path.toString()).map { plane ->
                    PlaneFactory.buildLandmark(plane, layerSettings, layerState)
                }
            }
        }
    }
}