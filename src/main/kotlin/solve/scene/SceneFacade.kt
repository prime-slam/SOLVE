package solve.scene

import javafx.beans.property.SimpleObjectProperty
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
import tornadofx.find

/**
 * Interaction interface of the scene to another parts of the application.
 * Transforms common application data models into scene data and applies it.
 */
object SceneFacade {
    val lastVisualizationKeepSettingsProperty = SimpleObjectProperty(false)
    private var lastVisualizationKeepSettings: Boolean
        get() = lastVisualizationKeepSettingsProperty.value
        private set(value) {
            lastVisualizationKeepSettingsProperty.value = value
        }

    private val controller: SceneController = find()

    private val visualizationLayers = HashMap<String, LayerSettings>()

    /**
     * Assigns unique color for layers, layer color is used when all landmarks have the same color.
     */
    private val layersColorManager = ColorManager<String>()

    /**
     * Transforms data and applies new project to the scene.
     *
     * @param layers list of used layers.
     * @param frames list of used frames, each frame should have files in the folder for each layer.
     * frame corresponds to an image with landmarks from different layers on top.
     * @param keepSettings is true when scale, columns number and another should not be reinitialized.
     */
    fun visualize(layers: List<ProjectLayer>, frames: List<ProjectFrame>, keepSettings: Boolean) {
        lastVisualizationKeepSettings = keepSettings

        val layersSettings = layers.map { projectLayer ->
            visualizationLayers[projectLayer.key] ?: projectLayer.toLayerSettings()
                .also { visualizationLayers[projectLayer.key] = it }
        } // reuse already created layer settings, it is needed mostly to keep settings on updates from catalog

        val layerStates = layers.map { projectLayer -> LayerState(projectLayer.name) }
        val visualizationFrames = frames.map { projectFrame -> projectFrame.toVisualizationFrame(layerStates) }
        val scene = Scene(visualizationFrames, layersSettings)
        controller.setScene(scene, keepSettings)
    }

    private fun ProjectLayer.toLayerSettings(): LayerSettings {
        return when (kind) {
            LayerKind.Keypoint -> LayerSettings.PointLayerSettings(this.name, this.key, layersColorManager)
            LayerKind.Line -> LayerSettings.LineLayerSettings(this.name, this.key, layersColorManager)
            LayerKind.Plane -> LayerSettings.PlaneLayerSettings(this.name, this.key, layersColorManager)
        }
    }

    private fun ProjectFrame.toVisualizationFrame(layerStates: List<LayerState>): VisualizationFrame {
        val getImage = {
            val stream = imagePath.toFile().inputStream()
            val image = Image(stream)
            stream.close()
            image
        }

        val layers = landmarkFiles.map { file ->
            val layerSettings = visualizationLayers[file.projectLayer.key]
                ?: throw IllegalStateException("No visualization layer is created for ${file.projectLayer.key}")
            val layerState = layerStates.single { x -> x.name == file.projectLayer.name }
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
