package solve.scene.view.association

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.paint.Color
import solve.scene.model.Landmark
import solve.scene.model.VisualizationFrame
import tornadofx.add
import tornadofx.toObservable
import solve.utils.structures.Size as DoubleSize

class AssociationsManager(
    private val frameSize: DoubleSize,
    private val framesIndent: Double,
    private val scale: DoubleProperty,
    private val frames: List<VisualizationFrame>,
    private val columnsNumber: Int,
    private val outOfFramesLayer: OutOfFramesLayer,
) {
    val chosenLayerName
        get() = firstFrameAssociationParameters?.key?.layerName
    private var firstFrameAssociationParameters: AssociationParameters? = null

    // Maps first frame and layer on it with a list of second frames and drawn shapes
    val drawnAssociations =
        mutableMapOf<AssociationKey, MutableMap<VisualizationFrame, List<AssociationLine>>>().toObservable()
    private val drawnAdorners = mutableMapOf<VisualizationFrame, AssociationAdorner>()

    fun initAssociation(associationParameters: AssociationParameters) {
        val firstFrame = firstFrameAssociationParameters?.key?.frame
        firstFrame?.apply { clearAdorner(firstFrame) }
        firstFrameAssociationParameters = associationParameters
        drawAdorner(associationParameters.key.frame)
    }

    fun associate(secondFrameParameters: AssociationParameters, colorProperty: ObjectProperty<Color>) {
        val firstFrameParameters = firstFrameAssociationParameters ?: return
        val firstFrame = firstFrameParameters.key.frame
        val secondFrame = secondFrameParameters.key.frame

        clearAdorner(firstFrame)

        val isAlreadyAssociated = isAlreadyAssociated(firstFrameParameters.key, secondFrame)
        if (isAlreadyAssociated || firstFrame == secondFrame) {
            firstFrameAssociationParameters = null
            return
        }

        associate(
            firstFrameParameters.key,
            secondFrameParameters.key,
            firstFrameParameters.landmarks,
            secondFrameParameters.landmarks,
            colorProperty
        )
        firstFrameAssociationParameters = null
    }

    private fun isAlreadyAssociated(associationKey: AssociationKey, secondFrame: VisualizationFrame) =
        drawnAssociations[associationKey]?.containsKey(secondFrame) == true

    private fun drawAdorner(frame: VisualizationFrame) {
        val framePosition = getFramePosition(frame)
        val adorner = AssociationAdorner(frameSize.width, frameSize.height, framePosition, scale)
        drawnAdorners[frame] = adorner
        outOfFramesLayer.add(adorner.node)
    }

    private fun clearAdorner(frame: VisualizationFrame) {
        outOfFramesLayer.children.remove(drawnAdorners[frame]?.node)
        drawnAdorners.remove(frame)
    }

    private fun associate(
        firstKey: AssociationKey,
        secondKey: AssociationKey,
        firstLandmarks: List<Landmark>,
        secondLandmarks: List<Landmark>,
        colorProperty: ObjectProperty<Color>
    ) {
        val firstFramePosition = getFramePosition(firstKey.frame)
        val secondFramePosition = getFramePosition(secondKey.frame)

        val lines = firstLandmarks.map { firstLandmark ->
            val firstKeypoint = firstLandmark as Landmark.Keypoint
            val secondKeypoint = secondLandmarks.firstOrNull { landmark ->
                val keypoint = landmark as Landmark.Keypoint
                keypoint.uid == firstKeypoint.uid
            } as? Landmark.Keypoint ?: return@map null

            AssociationLine(firstFramePosition, secondFramePosition, firstKeypoint, secondKeypoint, scale, colorProperty)
        }.filterNotNull()

        lines.forEach { line ->
            outOfFramesLayer.add(line.node)
        }

        drawnAssociations.putIfAbsent(firstKey, mutableMapOf())
        drawnAssociations[firstKey]?.set(secondKey.frame, lines)

        drawnAssociations.putIfAbsent(secondKey, mutableMapOf())
        drawnAssociations[secondKey]?.set(firstKey.frame, lines)
    }

    private fun getFramePosition(frame: VisualizationFrame): Pair<Double, Double> {
        val indexOfFrame = frames.indexOf(frame)
        val firstFrameRow = indexOfFrame / columnsNumber
        val firstFrameColumn = indexOfFrame % columnsNumber
        return Pair(firstFrameColumn * (frameSize.width + framesIndent), firstFrameRow * (frameSize.height + framesIndent))
    }

    fun clearAssociation(key: AssociationKey) {
        drawnAssociations[key]?.forEach { (frame, lines) ->
            val secondKey = AssociationKey(frame, key.layerName)
            drawnAssociations.remove(secondKey)
            lines.forEach { line ->
                outOfFramesLayer.children.remove(line.node)
                line.dispose()
            }
        }
        drawnAssociations.remove(key)
    }

    data class AssociationKey(val frame: VisualizationFrame, val layerName: String)

    data class AssociationParameters(
        val key: AssociationKey, val landmarks: List<Landmark.Keypoint>
    )
}
