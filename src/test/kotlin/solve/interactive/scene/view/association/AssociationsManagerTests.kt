package solve.interactive.scene.view.association

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import solve.interactive.InteractiveTestClass
import solve.scene.model.Point
import solve.scene.view.association.Associatable
import solve.scene.view.association.AssociationsManager
import solve.scene.view.association.OutOfFramesLayer
import solve.testMemoryLeak
import solve.utils.structures.DoublePoint
import solve.utils.structures.Size
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class AssociationsManagerTests : InteractiveTestClass() {
    private class TestAssociatable(override val coordinate: Point, override val uid: Long) : Associatable

    private lateinit var frameSize: Size
    private var frameIndent = 0.0
    private lateinit var scaleProperty: DoubleProperty
    private lateinit var frames: List<Int>
    private lateinit var columnsNumber: IntegerProperty
    private lateinit var outOfFramesLayer: OutOfFramesLayer
    private lateinit var associationsManager: AssociationsManager<Int, TestAssociatable>

    private val frameWidth = 200.0
    private val frameHeight = 100.0

    @BeforeEach
    fun setUp() {
        frameSize = Size(frameWidth, frameHeight)
        frameIndent = 10.0
        scaleProperty = SimpleDoubleProperty(1.0)
        frames = (0 until 100).toMutableList()
        columnsNumber = SimpleIntegerProperty(10)
        outOfFramesLayer = OutOfFramesLayer()
        associationsManager =
            AssociationsManager(frameSize, frameIndent, scaleProperty, frames, columnsNumber, outOfFramesLayer)
    }

    @Test
    fun `Choose first association frame`() {
        val layerName = "kp1"
        val frameNumber = 25
        val associationKey = AssociationsManager.AssociationKey(frameNumber, layerName)
        val landmarks = listOf(TestAssociatable(Point(1, 1), 1), TestAssociatable(Point(2, 2), 2))
        val associationParameters = AssociationsManager.AssociationParameters(associationKey, landmarks)

        associationsManager.initAssociation(associationParameters)

        val adorner = outOfFramesLayer.children.single()
        val expectedRowIndex = 2
        val expectedColumnIndex = 5
        assertEquals((frameHeight + frameIndent) * expectedRowIndex, adorner.layoutY)
        assertEquals((frameWidth + frameIndent) * expectedColumnIndex, adorner.layoutX)
        assertEquals(0, associationsManager.drawnAssociations.size)
        assertEquals(layerName, associationsManager.chosenLayerName)
    }

    @Test
    fun `Associate two frames from one row`() {
        val framesRow = 2
        val firstFrameColumn = 5
        val secondFrameColumn = 7

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )

        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )

        testAssociation(
            framesRow,
            framesRow,
            firstFrameColumn,
            secondFrameColumn,
            firstFrameLandmarks,
            secondFrameLandmarks
        )

        assertNull(associationsManager.chosenLayerName)
        assertTrue(outOfFramesLayer.children.all { it is Line }, "Adorner is drawn after association is done")
    }

    @Test
    fun `Associate two frames different rows`() {
        val firstFrameRow = 2
        val firstFrameColumn = 5
        val secondFrameRow = 4
        val secondFrameColumn = 7

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )

        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )

        testAssociation(
            firstFrameRow,
            secondFrameRow,
            firstFrameColumn,
            secondFrameColumn,
            firstFrameLandmarks,
            secondFrameLandmarks
        )
    }

    @Test
    fun `Associate two frames if not all points are matching`() {
        val firstFrameRow = 2
        val firstFrameColumn = 5
        val secondFrameRow = 4
        val secondFrameColumn = 7

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2),
            TestAssociatable(Point(2, 2), 3)
        )

        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2),
            TestAssociatable(Point(2, 2), 4)
        )

        testAssociation(
            firstFrameRow,
            secondFrameRow,
            firstFrameColumn,
            secondFrameColumn,
            firstFrameLandmarks,
            secondFrameLandmarks
        )
    }

    @Test
    fun `Associate frame with itself`() {
        val frameNumber = 25
        val landmarks = listOf(TestAssociatable(Point(1, 1), 1), TestAssociatable(Point(2, 2), 2))

        associateTwoFrames(frameNumber, frameNumber, landmarks, landmarks)

        assertNull(associationsManager.chosenLayerName)
        assertEquals(0, outOfFramesLayer.children.size)
    }

    @Test
    fun `Associate frame with a few frames`() {
        val firstFrameRow = 2
        val firstFrameColumn = 5
        val secondFrameRow = 4
        val secondFrameColumn = 7
        val thirdFrameRow = 3
        val thirdFrameColumn = 6

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )
        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )
        val thirdFrameLandmarks = listOf(
            TestAssociatable(Point(1, 2), 1),
            TestAssociatable(Point(10, 10), 2)
        )

        testAssociation(
            firstFrameRow,
            secondFrameRow,
            firstFrameColumn,
            secondFrameColumn,
            firstFrameLandmarks,
            secondFrameLandmarks
        )

        testAssociation(
            firstFrameRow,
            thirdFrameRow,
            firstFrameColumn,
            thirdFrameColumn,
            firstFrameLandmarks,
            thirdFrameLandmarks,
            listOf(47)
        )
    }

    @Test
    fun `Associate multiple layers`() {
        val firstLayerName = "kp1"
        val secondLayerName = "kp2"
        val row = 1
        val firstFrameColumn = 5
        val secondFrameColumn = 7

        val firstLayerFirstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )
        val secondLayerFirstFrameLandmarks = listOf(
            TestAssociatable(Point(4, 2), 1),
            TestAssociatable(Point(111, 5), 2)
        )

        val firstLayerSecondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )
        val secondLayerSecondFrameLandmarks = listOf(
            TestAssociatable(Point(46, 21), 1),
            TestAssociatable(Point(11, 35), 2)
        )

        testAssociation(
            row,
            row,
            firstFrameColumn,
            secondFrameColumn,
            firstLayerFirstFrameLandmarks,
            firstLayerSecondFrameLandmarks,
            layerName = firstLayerName
        )
        testAssociation(
            row,
            row,
            firstFrameColumn,
            secondFrameColumn,
            secondLayerFirstFrameLandmarks,
            secondLayerSecondFrameLandmarks,
            layerName = secondLayerName
        )
    }

    @Test
    fun `Clear association from the source frame`() {
        val row = 2
        val firstFrameColumn = 5
        val secondFrameColumn = 6
        val firstFrameNumber = row * columnsNumber.value + firstFrameColumn
        val secondFrameNumber = row * columnsNumber.value + secondFrameColumn

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )
        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )

        val layerName = "kp1"
        val firstFrameAssociationKey = AssociationsManager.AssociationKey(firstFrameNumber, layerName)

        associateTwoFrames(
            firstFrameNumber,
            secondFrameNumber,
            firstFrameLandmarks,
            secondFrameLandmarks,
            layerName = layerName
        )

        associationsManager.clearAssociation(firstFrameAssociationKey)

        assertTrue(
            associationsManager.drawnAssociations.values.all { it.values.isEmpty() },
            "There are not cleared associations"
        )
    }

    @Test
    fun `Clear association from the target frame`() {
        val row = 2
        val firstFrameColumn = 5
        val secondFrameColumn = 6
        val firstFrameNumber = row * columnsNumber.value + firstFrameColumn
        val secondFrameNumber = row * columnsNumber.value + secondFrameColumn

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )
        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )

        val layerName = "kp1"
        val secondFrameAssociationKey = AssociationsManager.AssociationKey(secondFrameNumber, layerName)

        associateTwoFrames(
            firstFrameNumber,
            secondFrameNumber,
            firstFrameLandmarks,
            secondFrameLandmarks,
            layerName = layerName
        )

        associationsManager.clearAssociation(secondFrameAssociationKey)

        assertTrue(
            associationsManager.drawnAssociations.values.all { it.values.isEmpty() },
            "There are not cleared associations"
        )
    }

    @Test
    fun `Clear associations from the frame associated with many frames`() {
        val firstFrameNumber = 25
        val secondFrameNumber = 27
        val thirdFrameNumber = 26

        val layerName = "kp1"

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )
        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )
        val thirdFrameLandmarks = listOf(
            TestAssociatable(Point(1, 2), 1),
            TestAssociatable(Point(10, 10), 2)
        )

        val firstFrameAssociationKey = AssociationsManager.AssociationKey(firstFrameNumber, layerName)
        associateTwoFrames(
            firstFrameNumber,
            secondFrameNumber,
            firstFrameLandmarks,
            secondFrameLandmarks,
            layerName = layerName
        )
        associateTwoFrames(
            firstFrameNumber,
            thirdFrameNumber,
            firstFrameLandmarks,
            thirdFrameLandmarks,
            layerName = layerName
        )

        associationsManager.clearAssociation(firstFrameAssociationKey)
        assertTrue(
            associationsManager.drawnAssociations.values.all { it.values.isEmpty() },
            "There are not cleared associations"
        )
    }

    @Test
    fun `Clear association in one layer from the frame with many associated layers`() {
        val firstLayerName = "kp1"
        val secondLayerName = "kp2"
        val firstFrameNumber = 25
        val secondFrameNumber = 27

        val firstLayerFirstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )
        val secondLayerFirstFrameLandmarks = listOf(
            TestAssociatable(Point(4, 2), 1),
            TestAssociatable(Point(111, 5), 2)
        )

        val firstLayerSecondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )
        val secondLayerSecondFrameLandmarks = listOf(
            TestAssociatable(Point(46, 21), 1),
            TestAssociatable(Point(11, 35), 2)
        )

        associateTwoFrames(
            firstFrameNumber,
            secondFrameNumber,
            firstLayerFirstFrameLandmarks,
            firstLayerSecondFrameLandmarks,
            layerName = firstLayerName
        )
        associateTwoFrames(
            firstFrameNumber,
            secondFrameNumber,
            secondLayerFirstFrameLandmarks,
            secondLayerSecondFrameLandmarks,
            layerName = secondLayerName
        )

        val firstFrameFirstLayerAssociationKey = AssociationsManager.AssociationKey(firstFrameNumber, firstLayerName)
        val firstFrameSecondLayerAssociationKey = AssociationsManager.AssociationKey(firstFrameNumber, secondLayerName)

        associationsManager.clearAssociation(firstFrameFirstLayerAssociationKey)
        assertEquals(1, associationsManager.drawnAssociations.keys.count { it.frame == firstFrameNumber })
        assertNotNull(associationsManager.drawnAssociations[firstFrameSecondLayerAssociationKey])
        assertNotNull(associationsManager.drawnAssociations[firstFrameSecondLayerAssociationKey]!![secondFrameNumber])

        associationsManager.clearAssociation(firstFrameSecondLayerAssociationKey)
        assertTrue(
            associationsManager.drawnAssociations.values.all { it.values.isEmpty() },
            "There are not cleared associations"
        )
    }

    @Test
    fun `Remove one of multiple associations from the frame`() {
        val firstFrameNumber = 25
        val secondFrameNumber = 27
        val thirdFrameNumber = 26

        val layerName = "kp1"

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )
        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )
        val thirdFrameLandmarks = listOf(
            TestAssociatable(Point(1, 2), 1),
            TestAssociatable(Point(10, 10), 2)
        )

        val firstFrameAssociationsKey = AssociationsManager.AssociationKey(firstFrameNumber, layerName)
        val thirdFrameAssociationsKey = AssociationsManager.AssociationKey(thirdFrameNumber, layerName)
        associateTwoFrames(
            firstFrameNumber,
            secondFrameNumber,
            firstFrameLandmarks,
            secondFrameLandmarks,
            layerName = layerName
        )
        associateTwoFrames(
            firstFrameNumber,
            thirdFrameNumber,
            firstFrameLandmarks,
            thirdFrameLandmarks,
            layerName = layerName
        )

        associationsManager.clearAssociation(thirdFrameAssociationsKey)
        assertNull(associationsManager.drawnAssociations[thirdFrameAssociationsKey])

        assertEquals(1, associationsManager.drawnAssociations[firstFrameAssociationsKey]!!.size)
        val firstToSecondFrameAssociation =
            associationsManager.drawnAssociations[firstFrameAssociationsKey]!![secondFrameNumber]!!
        assertEquals(2, firstToSecondFrameAssociation.size)

        associationsManager.clearAssociation(firstFrameAssociationsKey)
        assertTrue(
            associationsManager.drawnAssociations.values.all { it.values.isEmpty() },
            "There are not cleared associations"
        )
    }

    @Test
    fun `Association lines update stroke if layer color changed`() {
        val initialColor = c("F0F0F0")
        val colorProperty = SimpleObjectProperty(initialColor)
        val newColor = c("0F0F0F")

        val line = createAssociationLine(2, 2, 5, 7, Point(1, 1), Point(1, 2), colorProperty = colorProperty)
        assertEquals(initialColor, line.stroke)
        colorProperty.value = newColor
        assertEquals(newColor, line.stroke)
    }

    @Test
    fun `Association lines update visibility if layer enabled changed`() {
        val enabledProperty = SimpleBooleanProperty(false)
        val line = createAssociationLine(2, 2, 5, 7, Point(1, 1), Point(1, 2), enabledProperty = enabledProperty)
        assertFalse(line.isVisible)
        enabledProperty.value = true
        assertTrue(line.isVisible)
    }

    @Test
    fun `Association lines keep right position on scaling`() {
        scaleProperty.value = 2.0
        val firstFrameRow = 2
        val secondFrameRow = 4
        val firstFrameColumn = 3
        val secondFrameColumn = 7
        val firstFramePosition =
            DoublePoint((frameWidth + frameIndent) * firstFrameColumn, (frameHeight + frameIndent) * firstFrameRow)
        val secondFramePosition =
            DoublePoint((frameWidth + frameIndent) * secondFrameColumn, (frameHeight + frameIndent) * secondFrameRow)
        val startPosition = Point(1, 10)
        val finishPosition = Point(15, 25)

        val line = createAssociationLine(
            firstFrameRow,
            secondFrameRow,
            firstFrameColumn,
            secondFrameColumn,
            startPosition,
            finishPosition
        )
        assertEquals((firstFramePosition.x + startPosition.x) * scaleProperty.value, line.startX)
        assertEquals((firstFramePosition.y + startPosition.y) * scaleProperty.value, line.startY)
        assertEquals((secondFramePosition.x + finishPosition.x) * scaleProperty.value, line.endX)
        assertEquals((secondFramePosition.y + finishPosition.y) * scaleProperty.value, line.endY)

        scaleProperty.value = 0.4
        assertEquals((firstFramePosition.x + startPosition.x) * scaleProperty.value, line.startX)
        assertEquals((firstFramePosition.y + startPosition.y) * scaleProperty.value, line.startY)
        assertEquals((secondFramePosition.x + finishPosition.x) * scaleProperty.value, line.endX)
        assertEquals((secondFramePosition.y + finishPosition.y) * scaleProperty.value, line.endY)
    }

    @Test
    fun `Choose second frame if the first is not selected`() {
        val secondFrameAssociationKey = AssociationsManager.AssociationKey(27, "kp1")
        val secondFrameAssociationParameters = AssociationsManager.AssociationParameters(
            secondFrameAssociationKey,
            listOf(TestAssociatable(Point(1, 1), 1))
        )
        val colorProperty = SimpleObjectProperty(c("FF00FF"))
        val enabledProperty = SimpleBooleanProperty()

        assertDoesNotThrow {
            associationsManager.associate(
                secondFrameAssociationParameters,
                colorProperty,
                enabledProperty
            )
        }
    }

    @Test
    fun `Associate same frames and layers twice`() {
        val framesRow = 2
        val firstFrameColumn = 5
        val secondFrameColumn = 7

        val firstFrameLandmarks = listOf(
            TestAssociatable(Point(1, 1), 1),
            TestAssociatable(Point(130, 55), 2)
        )

        val secondFrameLandmarks = listOf(
            TestAssociatable(Point(10, 4), 1),
            TestAssociatable(Point(13, 77), 2)
        )

        testAssociation(
            framesRow,
            framesRow,
            firstFrameColumn,
            secondFrameColumn,
            firstFrameLandmarks,
            secondFrameLandmarks
        )
        testAssociation(
            framesRow,
            framesRow,
            firstFrameColumn,
            secondFrameColumn,
            firstFrameLandmarks,
            secondFrameLandmarks
        )
    }

    @Test
    fun `Lines can be garbage collected when association is cleared`() {
        var line: Line? = createAssociationLine(2, 2, 5, 7, Point(1, 1), Point(1, 2))
        val factory = { line!! }
        testMemoryLeak(factory) {
            associationsManager.clearAssociation(AssociationsManager.AssociationKey(25, "kp1"))
            line = null
        }
    }

    @Test
    fun `Lines update position when columns number changed`() {
        val firstFrameNumber = 17
        val secondFrameNumber = 39
        val initialColumnsNumber = columnsNumber.value
        val line = createAssociationLine(
            firstFrameNumber / initialColumnsNumber,
            secondFrameNumber / initialColumnsNumber,
            firstFrameNumber % initialColumnsNumber,
            secondFrameNumber % initialColumnsNumber,
            Point(0, 0),
            Point(0, 0)
        )

        val newColumnsNumber = 5
        columnsNumber.value = newColumnsNumber

        assertEquals((firstFrameNumber / newColumnsNumber) * (frameHeight + frameIndent), line.endY)
        assertEquals((firstFrameNumber % newColumnsNumber) * (frameWidth + frameIndent), line.endX)
        assertEquals((secondFrameNumber / newColumnsNumber) * (frameHeight + frameIndent), line.startY)
        assertEquals((secondFrameNumber % newColumnsNumber) * (frameWidth + frameIndent), line.startX)
    }

    @Test
    fun `Can be garbage collected after dispose`() {
        val factory = { associationsManager }
        testMemoryLeak(factory) {
            associationsManager.dispose()
            associationsManager =
                AssociationsManager(frameSize, frameIndent, scaleProperty, frames, columnsNumber, outOfFramesLayer)
        }
    }

    private fun createAssociationLine(
        firstFrameRow: Int,
        secondFrameRow: Int,
        firstFrameColumn: Int,
        secondFrameColumn: Int,
        startPosition: Point,
        finishPosition: Point,
        colorProperty: ObjectProperty<Color> = SimpleObjectProperty(c("F0F0F0")),
        enabledProperty: BooleanProperty = SimpleBooleanProperty(true)
    ): Line {
        val firstFrameLandmarks = listOf(
            TestAssociatable(startPosition, 1)
        )

        val secondFrameLandmarks = listOf(
            TestAssociatable(finishPosition, 1)
        )

        associateTwoFrames(
            firstFrameRow * columnsNumber.value + firstFrameColumn,
            secondFrameRow * columnsNumber.value + secondFrameColumn,
            firstFrameLandmarks,
            secondFrameLandmarks,
            colorProperty = colorProperty,
            enabledProperty = enabledProperty
        )

        return associationsManager.drawnAssociations.values.first().values.single().single().node as Line
    }

    private fun testAssociation(
        firstFrameRow: Int,
        secondFrameRow: Int,
        firstFrameColumn: Int,
        secondFrameColumn: Int,
        firstFrameLandmarks: List<TestAssociatable>,
        secondFrameLandmarks: List<TestAssociatable>,
        alreadyAssociatedFrames: List<Int> = listOf(),
        layerName: String = "kp1"
    ) {
        val firstFrameNumber = firstFrameRow * columnsNumber.value + firstFrameColumn
        val secondFrameNumber = secondFrameRow * columnsNumber.value + secondFrameColumn

        val firstFrameAssociationKey = AssociationsManager.AssociationKey(firstFrameNumber, layerName)
        associateTwoFrames(
            firstFrameNumber,
            secondFrameNumber,
            firstFrameLandmarks,
            secondFrameLandmarks,
            layerName = layerName
        )

        assertEquals(null, associationsManager.chosenLayerName)
        val associatedFramesNumbers = associationsManager.drawnAssociations.keys.map { it.frame }
        assertEquals(
            setOf(firstFrameNumber, secondFrameNumber) + alreadyAssociatedFrames.toSet(),
            associatedFramesNumbers.toSet()
        )
        val lines =
            (associationsManager.drawnAssociations[firstFrameAssociationKey] ?: fail())[secondFrameNumber] ?: fail()

        val secondFrameUids = secondFrameLandmarks.map { it.uid }
        val matchingUids = firstFrameLandmarks.map { it.uid }.filter { secondFrameUids.contains(it) }

        assertEquals(matchingUids.size, lines.count())

        val firstFrameX = (frameWidth + frameIndent) * firstFrameColumn
        val firstFrameY = (frameHeight + frameIndent) * firstFrameRow
        val secondFrameX = (frameWidth + frameIndent) * secondFrameColumn
        val secondFrameY = (frameHeight + frameIndent) * secondFrameRow

        val linesNodes = lines.map { it.node as Line }

        matchingUids.forEach { uid ->
            val firstFrameLandmarkPosition = firstFrameLandmarks.single { it.uid == uid }.coordinate
            val secondFrameFirstLandmarkPosition = secondFrameLandmarks.single { it.uid == uid }.coordinate

            assertEquals(
                1,
                linesNodes.count {
                    it.startX == firstFrameX + firstFrameLandmarkPosition.x
                            && it.startY == firstFrameY + firstFrameLandmarkPosition.y
                            && it.endX == secondFrameX + secondFrameFirstLandmarkPosition.x
                            && it.endY == secondFrameY + secondFrameFirstLandmarkPosition.y
                },
                "No line was drawn for landmark with uid: $uid"
            )
        }
    }

    private fun associateTwoFrames(
        firstFrameNumber: Int,
        secondFrameNumber: Int,
        firstFrameLandmarks: List<TestAssociatable>,
        secondFrameLandmarks: List<TestAssociatable>,
        colorProperty: ObjectProperty<Color> = SimpleObjectProperty(c("F0F0F0")),
        enabledProperty: BooleanProperty = SimpleBooleanProperty(true),
        layerName: String = "kp1"
    ) {
        val firstFrameAssociationKey = AssociationsManager.AssociationKey(firstFrameNumber, layerName)
        val firstFrameAssociationParameters =
            AssociationsManager.AssociationParameters(firstFrameAssociationKey, firstFrameLandmarks)

        val secondFrameAssociationKey = AssociationsManager.AssociationKey(secondFrameNumber, layerName)
        val secondFrameAssociationParameters =
            AssociationsManager.AssociationParameters(secondFrameAssociationKey, secondFrameLandmarks)

        associationsManager.initAssociation(firstFrameAssociationParameters)
        associationsManager.associate(secondFrameAssociationParameters, colorProperty, enabledProperty)
    }
}
