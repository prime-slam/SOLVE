package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import solve.scene.model.*
import solve.scene.view.OutOfFramesLayer
import tornadofx.*

class AssociationsManager(
    private val frameWidth: Double,
    private val frameHeight: Double,
    private val framesIndent: Double,
    val scale: DoubleProperty,
    private val frames: List<VisualizationFrame>,
    private val columnsNumber: Int,
    private val outOfFramesLayer: OutOfFramesLayer,
) {
    private var prevScale = scale.value
    private var associationParameters: Pair<VisualizationFrame, Layer>? = null
    // Maps first frame and layer on it with a list of second frames and drawn shapes
    private var drawnShapes =
        mutableMapOf<Pair<VisualizationFrame, Layer>, MutableMap<VisualizationFrame, List<Line>>>()
    private var drawnAdorners = mutableMapOf<VisualizationFrame, AssociationAdorner>()

    init {
        scale.onChange { newScale ->
            val scaleFactor = newScale / prevScale
            doForAllShapes { line ->
                line.startX *= scaleFactor
                line.startY *= scaleFactor
                line.endX *= scaleFactor
                line.endY *= scaleFactor
            }

            drawnAdorners.values.forEach { adorner ->
                adorner.node.layoutX *= scaleFactor
                adorner.node.layoutY *= scaleFactor
            }

            prevScale = newScale
        }
    }

    fun initAssociation(frame: VisualizationFrame, layer: Layer) {
        outOfFramesLayer.children.remove(drawnAdorners[associationParameters?.first]?.node)
        drawnAdorners.remove(associationParameters?.first)
        associationParameters = Pair(frame, layer)

        val adorner = AssociationAdorner(frameWidth, frameHeight, scale)
        val framePosition = getFramePosition(frame)
        adorner.node.layoutX = framePosition.first * scale.value
        adorner.node.layoutY = framePosition.second * scale.value
        drawnAdorners[frame] = adorner
        outOfFramesLayer.add(adorner.node)
    }

    fun chooseFrame(frame: VisualizationFrame) {
        val firstFrameParameters = associationParameters ?: return

        outOfFramesLayer.children.remove(drawnAdorners[firstFrameParameters.first]?.node)
        drawnAdorners.remove(firstFrameParameters.first)

        if (firstFrameParameters.first == frame) {
            associationParameters = null
            return
        }

        associate(firstFrameParameters.first, frame, firstFrameParameters.second)
        associationParameters = null
    }

    private fun associate(firstFrame: VisualizationFrame, secondFrame: VisualizationFrame, layer: Layer) {
        val firstLandmarks = firstFrame.landmarks[layer]!! // TODO: current model structure is bad
        val secondLandmarks = secondFrame.landmarks[layer]!!

        val firstFramePosition = getFramePosition(firstFrame)
        val secondFramePosition = getFramePosition(secondFrame)

        val lines = firstLandmarks.map { firstLandmark ->
            val firstKeypoint = firstLandmark as Landmark.Keypoint
            val secondKeypoint = secondLandmarks.firstOrNull { landmark ->
                val keypoint = landmark as Landmark.Keypoint
                keypoint.uid == firstKeypoint.uid
            } as? Landmark.Keypoint ?: return@map null

            val line = Line()
            line.startX = (firstFramePosition.first + firstKeypoint.coordinate.x) * scale.value
            line.startY = (firstFramePosition.second + firstKeypoint.coordinate.y) * scale.value
            line.endX = (secondFramePosition.first + secondKeypoint.coordinate.x) * scale.value
            line.endY = (secondFramePosition.second + secondKeypoint.coordinate.y) * scale.value
            line.stroke = Color.RED
            line
        }

        lines.filterNotNull().forEach {
            outOfFramesLayer.add(it)
        }
        val key = Pair(firstFrame, layer)
        drawnShapes.putIfAbsent(key, mutableMapOf())
        drawnShapes[key]?.set(secondFrame, lines.filterNotNull())
    }

    private fun getFramePosition(frame: VisualizationFrame): Pair<Double, Double> {
        val indexOfFrame = frames.indexOf(frame)
        val firstFrameRow = indexOfFrame / columnsNumber
        val firstFrameColumn = indexOfFrame % columnsNumber
        return Pair(firstFrameColumn * (frameWidth + framesIndent), firstFrameRow * (frameHeight + framesIndent))
    }

    fun clearAssociation(frame: VisualizationFrame, layer: Layer) {
        drawnShapes[Pair(frame, layer)]?.values?.forEach { lines ->
            lines.forEach {
                line -> outOfFramesLayer.children.remove(line)
            }
        }
    }

    private fun doForAllShapes(delegate: (Line) -> Unit) =
        drawnShapes.values.forEach { associations ->
            associations.values.forEach { lines ->
                lines.forEach { line ->
                    delegate(line)
                }
            }
        }
}