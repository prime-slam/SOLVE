package solve.scene.view.association

import javafx.beans.InvalidationListener
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.paint.Color
import solve.utils.structures.DoublePoint
import solve.utils.structures.Size
import tornadofx.add
import tornadofx.toObservable

/**
 * Responsible for creating associations line between keypoints from selected frames and layers.
 *
 * @param T type of data objects backing to frames.
 * @param S type of objects, which will be associated, keypoints in our case.
 *
 * @param frameSize size of a frame on the scene without indent, used to calculate association lines positions.
 * @param framesIndent margin between frames on the scene, used to calculate association lines positions.
 * @param scale global scale property, used to recalculate associations lines positions when scale changed.
 * @param frames list of frames data, used to calculate frame column and row.
 * @param outOfFramesLayer pane, where association lines and frames adorners placed.
 */
class AssociationsManager<T, S : Associatable>(
    private val frameSize: Size,
    private val framesIndent: Double,
    private val scale: DoubleProperty,
    private val frames: List<T>,
    private val columnsNumber: IntegerProperty,
    private val outOfFramesLayer: OutOfFramesLayer
) {
    /**
     * name of the current selected layer.
     */
    val chosenLayerName
        get() = firstFrameAssociationParameters?.key?.layerName
    private var firstFrameAssociationParameters: AssociationParameters<T, S>? = null

    private val columnsNumberChangedListener = InvalidationListener {
        updateLinesPosition()
    }

    init {
        columnsNumber.addListener(columnsNumberChangedListener)
    }

    /**
     * Matches first association frame and chosen layer to a list of all associated second frames and drawn lines.
     *
     * Example:
     * drawnAssociations[AssociationKey(25, "keypoints12345")][27] returns list of drawn lines between 25 and 27 frames,
     * for landmarks in "keypoints12345" layer.
     */
    val drawnAssociations =
        mutableMapOf<AssociationKey<T>, MutableMap<T, List<AssociationLine>>>().toObservable()
    private val drawnAdorners = mutableMapOf<T, AssociationAdorner>()

    /**
     * Chooses the frame to associate keypoints of the specified layer with another.
     * Draws an adorner on top of the selected frame.
     */
    fun initAssociation(associationParameters: AssociationParameters<T, S>) {
        val firstFrame = firstFrameAssociationParameters?.key?.frame
        firstFrame?.apply { clearAdorner(firstFrame) }
        firstFrameAssociationParameters = associationParameters
        drawAdorner(associationParameters.key.frame)
    }

    /**
     * Draws lines between keypoints in previously selected layer on the first and second frames.
     *
     * The drawn adorner is cleared.
     *
     * @param colorProperty chosen layer color property from the settings, used to keep lines color the same to keypoints.
     * @param enabledProperty chosen layer enabled property from the settings, used to keep visibility of lines the same to keypoints.
     */
    fun associate(
        secondFrameParameters: AssociationParameters<T, S>,
        colorProperty: ObjectProperty<Color>,
        enabledProperty: BooleanProperty
    ) {
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
            colorProperty,
            enabledProperty
        )
        firstFrameAssociationParameters = null
    }

    private fun isAlreadyAssociated(associationKey: AssociationKey<T>, secondFrame: T) =
        drawnAssociations[associationKey]?.containsKey(secondFrame) == true

    private fun drawAdorner(frame: T) {
        val framePosition = getFramePosition(frame)
        val adorner = AssociationAdorner(frameSize.width, frameSize.height, framePosition, scale)
        drawnAdorners[frame] = adorner
        outOfFramesLayer.add(adorner.node)
    }

    private fun clearAdorner(frame: T) {
        val adorner = drawnAdorners[frame] ?: return
        outOfFramesLayer.children.remove(adorner.node)
        adorner.dispose()
        drawnAdorners.remove(frame)
    }

    private fun associate(
        firstKey: AssociationKey<T>,
        secondKey: AssociationKey<T>,
        firstLandmarks: List<S>,
        secondLandmarks: List<S>,
        colorProperty: ObjectProperty<Color>,
        enabledProperty: BooleanProperty
    ) {
        val firstFramePosition = getFramePosition(firstKey.frame)
        val secondFramePosition = getFramePosition(secondKey.frame)

        val lines = firstLandmarks.map { firstLandmark ->
            val secondLandmark = secondLandmarks.firstOrNull { landmark ->
                firstLandmark.uid == landmark.uid
            } ?: return@map null // not all keypoints are matching

            AssociationLine(
                firstFramePosition,
                secondFramePosition,
                firstLandmark.coordinate,
                secondLandmark.coordinate,
                scale,
                colorProperty,
                enabledProperty
            )
        }.filterNotNull()

        lines.forEach { line ->
            outOfFramesLayer.add(line.node)
        }

        drawnAssociations.putIfAbsent(firstKey, mutableMapOf())
        drawnAssociations[firstKey]?.set(secondKey.frame, lines)

        // should save association twice to make it possible to clear association from the target frame.
        drawnAssociations.putIfAbsent(secondKey, mutableMapOf())
        drawnAssociations[secondKey]?.set(firstKey.frame, lines)
    }

    private fun getFramePosition(frame: T): DoublePoint {
        val indexOfFrame = frames.indexOf(frame)
        val firstFrameRow = indexOfFrame / columnsNumber.value
        val firstFrameColumn = indexOfFrame % columnsNumber.value
        return DoublePoint(
            firstFrameColumn * (frameSize.width + framesIndent),
            firstFrameRow * (frameSize.height + framesIndent)
        )
    }

    /**
     * Clears associations of chosen layer and frame to all another frames.
     */
    fun clearAssociation(key: AssociationKey<T>) {
        drawnAssociations[key]?.forEach { (frame, lines) ->
            val secondKey = AssociationKey(frame, key.layerName)
            drawnAssociations[secondKey]?.remove(key.frame)
            lines.forEach { line ->
                outOfFramesLayer.children.remove(line.node)
                line.dispose()
            }
        }
        drawnAssociations.remove(key)
    }

    fun dispose() {
        columnsNumber.removeListener(columnsNumberChangedListener)
    }

    private fun updateLinesPosition() {
        drawnAssociations.forEach { (firstFrameKey, associations) ->
            associations.forEach { (secondFrame, lines) ->
                lines.forEach { line ->
                    line.updateFramesPosition(getFramePosition(firstFrameKey.frame), getFramePosition(secondFrame))
                }
            }
        }
    }

    /**
     * Data structure aimed to address associations.
     */
    data class AssociationKey<T>(val frame: T, val layerName: String)

    /**
     * Data structure, which aggregates association data.
     */
    data class AssociationParameters<T, S : Associatable>(
        val key: AssociationKey<T>,
        val landmarks: List<S>
    )
}
