package sliv.tool.scene

import javafx.scene.image.Image
import sliv.tool.project.model.*
import sliv.tool.scene.controller.SceneController
import sliv.tool.scene.model.*
import java.io.FileInputStream

class SceneFacade(private val controller: SceneController) {
    private val visualizationLayers = HashMap<String, Layer>()

    fun visualize(layers: List<ProjectLayer>, frames: List<ProjectFrame>) {
        layers.forEach { projectLayer ->
            if (!visualizationLayers.contains(projectLayer.name)) { //don't rewrite existing settings
                visualizationLayers[projectLayer.name] = projectLayer.toVisualizationLayer()
            }
        }
        val scene =
            Scene({ i -> frames[i].toVisualizationFrame() }, frames.count(), visualizationLayers.values.toList())
        controller.scene.value = scene
    }

    private fun ProjectLayer.toVisualizationLayer(): Layer {
        return when (kind) {
            LayerKind.KEYPOINT -> Layer.PointLayer(this.name)
            LayerKind.LINE -> Layer.LineLayer(this.name)
            LayerKind.PLANE -> Layer.PlaneLayer(this.name)
        }
    }

    private fun ProjectFrame.toVisualizationFrame(): VisualizationFrame {
        val image = Image(FileInputStream(this.imagePath.toFile()))
        val landmarks = HashMap<Layer, List<Landmark>>()
        landmarkFiles.forEach { file ->
            val visualizationLayer = visualizationLayers[file.projectLayer.name]
                ?: throw IllegalStateException("No visualization layer is created for ${file.projectLayer.name}")
            landmarks[visualizationLayer] = createLandmarks(file).toList()
        }
        return VisualizationFrame(image, this.timestamp, landmarks.toMap())
    }

    //TODO: use real parser instead of test data
    private fun createLandmarks(file: LandmarkFile): Sequence<Landmark> {
        val layer = visualizationLayers[file.projectLayer.name]!!
        return when (file.projectLayer.kind) {
            LayerKind.KEYPOINT -> sequence {
                for (i in 1..20) {
                    yield(
                        Landmark.Keypoint(
                            i.toLong(), layer as Layer.PointLayer, Point((i * 15).toShort(), (i * 15).toShort())
                        )
                    )
                }
            }

            LayerKind.LINE -> sequence {
                yield(
                    Landmark.Line(
                        10, layer as Layer.LineLayer, Point(0, 0), Point(10, 10)
                    )
                )
            }


            LayerKind.PLANE -> sequence {
                yield(
                    Landmark.Plane(
                        100,
                        layer as Layer.PlaneLayer,
                        (0..100).flatMap { x -> (0..100).map { y -> Point(x.toShort(), y.toShort()) } }.toTypedArray()
                    )
                )
            }
        }
    }
}