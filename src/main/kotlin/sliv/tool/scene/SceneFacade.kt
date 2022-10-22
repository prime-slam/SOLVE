package sliv.tool.scene

import javafx.scene.image.Image
import sliv.tool.project.model.*
import sliv.tool.scene.controller.SceneController
import sliv.tool.scene.model.*
import java.io.FileInputStream

class SceneFacade(private val controller: SceneController) {
    fun visualize(layers: List<ProjectLayer>, frames: List<ProjectFrame>) {
        val visualizationLayers = layers.map { x -> x.toVisualizationLayer() }
        val scene =
            Scene({ i -> frames[i].toVisualizationFrame(visualizationLayers) }, frames.count(), visualizationLayers)
        controller.scene.value = scene
    }

    private fun ProjectLayer.toVisualizationLayer(): Layer {
        return when (kind) {
            LayerKind.KEYPOINT -> Layer.PointLayer(this.name)
            LayerKind.LINE -> Layer.LineLayer(this.name)
            LayerKind.PLANE -> Layer.PlaneLayer(this.name)
        }
    }

    private fun ProjectFrame.toVisualizationFrame(layers: List<Layer>): VisualizationFrame {
        val image = Image(FileInputStream(this.imagePath.toFile()))
        val landmarks = HashMap<Layer, List<Landmark>>()
        this.landmarkFiles.forEach {
            landmarks[layers.first { layer -> layer.name == it.projectLayer.name }] =
                createLandmarks(it, layers).toList()
        }
        return VisualizationFrame(image, this.timestamp, landmarks.toMap())
    }

    //TODO: use real parser instead of test data
    private fun createLandmarks(file: LandmarkFile, layers: List<Layer>): Sequence<Landmark> {
        val layer = layers.first { layer -> layer.name == file.projectLayer.name }
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