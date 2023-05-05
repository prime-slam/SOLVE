package solve.interactive.scene.view.association

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
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
import solve.scene.view.association.AssociationLine
import solve.testMemoryLeak
import solve.utils.structures.DoublePoint
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class AssociationLineTests : InteractiveTestClass() {
    private lateinit var firstFramePosition: DoublePoint
    private lateinit var secondFramePosition: DoublePoint
    private lateinit var firstKeypointCoordinate: Point
    private lateinit var secondKeypointCoordinate: Point
    private lateinit var scale: SimpleDoubleProperty
    private lateinit var initialColor: Color
    private lateinit var colorProperty: SimpleObjectProperty<Color>
    private lateinit var enabledProperty: SimpleBooleanProperty
    private lateinit var associationLine: AssociationLine

    @BeforeEach
    fun setUp() {
        firstFramePosition = DoublePoint(0.0, 0.0)
        secondFramePosition = DoublePoint(100.0, 150.0)
        firstKeypointCoordinate = Point(0, 0)
        secondKeypointCoordinate = Point(15, 25)
        scale = SimpleDoubleProperty(1.0)
        initialColor = c("F0F0F0")
        colorProperty = SimpleObjectProperty(initialColor)
        enabledProperty = SimpleBooleanProperty(true)
        associationLine = createAssociationLine()
    }

    @Test
    fun `Updates stroke when color changed`() {
        val line = associationLine.node as Line
        assertEquals(initialColor, line.stroke)

        val newColor = c("0FFF00")
        colorProperty.value = newColor
        assertEquals(newColor, line.stroke)
    }

    @Test
    fun `Updates visibility when enabled changed`() {
        val line = associationLine.node as Line
        assertEquals(enabledProperty.value, line.isVisible)

        enabledProperty.value = false
        assertEquals(false, line.isVisible)
    }

    @Test
    fun `Correct coordinates within the association layer pane`() {
        val line = associationLine.node as Line
        assertEquals(firstFramePosition.x + firstKeypointCoordinate.x, line.layoutX)
        assertEquals(firstFramePosition.y + firstKeypointCoordinate.y, line.layoutY)
    }

    @Test
    fun `Updates coordinates within the association layer pane when scale changed`() {
        val line = associationLine.node as Line
        scale.value *= 2
        assertEquals(firstFramePosition.x * scale.value + firstKeypointCoordinate.x * scale.value, line.layoutX)
        assertEquals(firstFramePosition.y * scale.value + firstKeypointCoordinate.y * scale.value, line.layoutY)
    }

    @Test
    fun `Line can be garbage collected after dispose`() {
        val factory = { createAssociationLine() }
        val action: (AssociationLine) -> Unit = { line -> line.dispose() }
        testMemoryLeak(factory, action)
    }

    private fun createAssociationLine() = AssociationLine(
        firstFramePosition,
        secondFramePosition,
        firstKeypointCoordinate,
        secondKeypointCoordinate,
        scale,
        colorProperty,
        enabledProperty
    )
}