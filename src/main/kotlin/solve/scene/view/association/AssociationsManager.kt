package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import solve.scene.model.*
import tornadofx.*

class AssociationsManager(
    private val frameWidth: Double,
    private val frameHeight: Double,
    private val framesIndent: Double,
    private val scale: DoubleProperty,
    private val frames: List<VisualizationFrame>,
    private val columnsNumber: Int,
    private val outOfFramesLayer: OutOfFramesLayer,
) {
    private var associationParameters: Pair<VisualizationFrame, List<Landmark.Keypoint>>? = null

    // Maps first frame and layer on it with a list of second frames and drawn shapes
    private var drawnShapes =
        mutableMapOf<VisualizationFrame, MutableMap<VisualizationFrame, List<AssociationLine>>>() // TODO: more than one layer in a frame
    private var drawnAdorners = mutableMapOf<VisualizationFrame, AssociationAdorner>()

    fun initAssociation(frame: VisualizationFrame, landmarks: List<Landmark.Keypoint>) {
        val firstFrame = associationParameters?.first
        if (firstFrame != null) {
            clearAdorner(firstFrame)
        }

        associationParameters = Pair(frame, landmarks)
        drawAdorner(frame)
    }

    fun chooseFrame(frame: VisualizationFrame, landmarks: List<Landmark.Keypoint>) {
        val firstFrameParameters = associationParameters ?: return

        clearAdorner(firstFrameParameters.first)

        if (firstFrameParameters.first == frame || isAlreadyAssociated(firstFrameParameters.first, frame)) {
            associationParameters = null
            return
        }

        associate(firstFrameParameters.first, frame, firstFrameParameters.second, landmarks)
        associationParameters = null
    }

    private fun isAlreadyAssociated(firstFrame: VisualizationFrame, secondFrame: VisualizationFrame) =
        drawnShapes[firstFrame]?.containsKey(secondFrame) == true

    private fun drawAdorner(frame: VisualizationFrame) {
        val framePosition = getFramePosition(frame)
        val adorner = AssociationAdorner(frameWidth, frameHeight, framePosition, scale)
        drawnAdorners[frame] = adorner
        outOfFramesLayer.add(adorner.node)
    }

    private fun clearAdorner(frame: VisualizationFrame) {
        outOfFramesLayer.children.remove(drawnAdorners[frame]?.node)
        drawnAdorners.remove(frame)
    }

    private fun associate(
        firstFrame: VisualizationFrame,
        secondFrame: VisualizationFrame,
        firstLandmarks: List<Landmark>,
        secondLandmarks: List<Landmark>
    ) {
        val firstFramePosition = getFramePosition(firstFrame)
        val secondFramePosition = getFramePosition(secondFrame)

        val lines = firstLandmarks.map { firstLandmark ->
            val firstKeypoint = firstLandmark as Landmark.Keypoint
            val secondKeypoint = secondLandmarks.firstOrNull { landmark ->
                val keypoint = landmark as Landmark.Keypoint
                keypoint.uid == firstKeypoint.uid
            } as? Landmark.Keypoint ?: return@map null

            AssociationLine(firstFramePosition, secondFramePosition, firstKeypoint, secondKeypoint, scale)
        }.filterNotNull()

        lines.forEach { line ->
            outOfFramesLayer.add(line.node)
        }

        drawnShapes.putIfAbsent(firstFrame, mutableMapOf())
        drawnShapes[firstFrame]?.set(secondFrame, lines)
    }

    private fun getFramePosition(frame: VisualizationFrame): Pair<Double, Double> {
        val indexOfFrame = frames.indexOf(frame)
        val firstFrameRow = indexOfFrame / columnsNumber
        val firstFrameColumn = indexOfFrame % columnsNumber
        return Pair(firstFrameColumn * (frameWidth + framesIndent), firstFrameRow * (frameHeight + framesIndent))
    }

    fun clearAssociation(frame: VisualizationFrame) {
        drawnShapes[frame]?.values?.forEach { lines ->
            lines.forEach { line ->
                outOfFramesLayer.children.remove(line.node)
            }
        }
        drawnShapes.remove(frame)
    }
}