package solve.interactive.scene.view.association

import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import solve.interactive.InteractiveTestClass
import solve.scene.view.association.AssociationAdorner
import solve.utils.structures.DoublePoint

@ExtendWith(ApplicationExtension::class)
internal class AssociationAdornerTests : InteractiveTestClass() {
    @Test
    fun `Adorner rectangle fills the corresponding frame`() {
        val width = 10.0
        val height = 5.0
        val position = DoublePoint(20.0, 30.0)
        val scale = SimpleDoubleProperty(1.0)
        val adorner = AssociationAdorner(width, height, position, scale)
        assertEquals(position.x, adorner.node.layoutX)
        assertEquals(position.y, adorner.node.layoutY)
        val pane = adorner.node as Pane
        val rect = pane.children.filterIsInstance<Rectangle>().single()
        assertEquals(width, rect.width)
        assertEquals(height, rect.height)
    }

    @Test
    fun `Scale adorner`() {
        val width = 10.0
        val height = 5.0
        val position = DoublePoint(20.0, 30.0)
        val scale = SimpleDoubleProperty(2.0)
        val adorner = AssociationAdorner(width, height, position, scale)
        assertEquals(position.x * scale.value, adorner.node.layoutX)
        assertEquals(position.y * scale.value, adorner.node.layoutY)
        val pane = adorner.node as Pane
        val rect = pane.children.filterIsInstance<Rectangle>().single()
        assertEquals(width * scale.value, rect.width)
        assertEquals(height * scale.value, rect.height)

        scale.value = 0.5

        assertEquals(position.x * scale.value, adorner.node.layoutX)
        assertEquals(position.y * scale.value, adorner.node.layoutY)
        assertEquals(width * scale.value, rect.width)
        assertEquals(height * scale.value, rect.height)
    }
}