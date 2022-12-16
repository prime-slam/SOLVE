package solve.scene.view

import javafx.beans.property.DoubleProperty
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import solve.scene.model.*
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
    private var drawnShapes = mutableMapOf<Pair<VisualizationFrame, Layer>, List<Line>>()
    private var drawnAdorners = mutableMapOf<VisualizationFrame, Rectangle>()

    init {
        scale.onChange { newScale ->
            val scaleFactor = newScale / prevScale
            doForAllShapes { line ->
                line.startX *= scaleFactor
                line.startY *= scaleFactor
                line.endX *= scaleFactor
                line.endY *= scaleFactor
            }

            drawnAdorners.values.forEach { rect ->
                rect.layoutX *= scaleFactor
                rect.layoutY *= scaleFactor
                rect.width *= scaleFactor
                rect.height *= scaleFactor
            }

            prevScale = newScale
        }
    }

    fun initAssociation(frame: VisualizationFrame, layer: Layer) {
        associationParameters = Pair(frame, layer)
        val rect = Rectangle()
        val framePosition = getFramePosition(frame)
        rect.layoutX = framePosition.first * scale.value
        rect.layoutY = framePosition.second * scale.value
        rect.width = frameWidth * scale.value
        rect.height = frameHeight * scale.value
        rect.fill = Color.BLACK
        rect.opacity = 0.5
        drawnAdorners[frame] = rect
        outOfFramesLayer.add(rect)
    }

    fun chooseFrame(frame: VisualizationFrame) {
        val firstFrameParameters = associationParameters ?: return

        outOfFramesLayer.children.remove(drawnAdorners[firstFrameParameters.first])
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
        drawnShapes[key] = lines.filterNotNull()
    }

    private fun getFramePosition(frame: VisualizationFrame): Pair<Double, Double> {
        val indexOfFrame = frames.indexOf(frame)
        val firstFrameRow = indexOfFrame / columnsNumber
        val firstFrameColumn = indexOfFrame % columnsNumber
        return Pair(firstFrameColumn * (frameWidth + framesIndent), firstFrameRow * (frameHeight + framesIndent))
    }

    fun clearAssociation(frame: VisualizationFrame, layer: Layer) {
        drawnShapes[Pair(frame, layer)]?.forEach { line ->
            outOfFramesLayer.children.remove(line)
        }
    }

    private fun doForAllShapes(delegate: (Line) -> Unit) =
        drawnShapes.values.forEach { lines ->
            lines.forEach { line ->
                delegate(line)
            }
        }
}