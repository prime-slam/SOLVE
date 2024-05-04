package solve.interactive.scene.view.association

import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import solve.interactive.InteractiveTestClass
import solve.scene.view.association.AssociationAdorner
import solve.testMemoryLeak
import solve.utils.structures.DoublePoint
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
internal class AssociationConnectionAdornerTests : InteractiveTestClass() {
    @Test
    fun `Adorner rectangle fills the corresponding frame`() {
        val width = 10.0
        val height = 5.0
        val getPosition = { DoublePoint(20.0, 30.0) }
        val getScale = { 1.0 }
        val adorner = AssociationAdorner(width, height, getPosition, getScale)
        assertEquals(getPosition().x, adorner.node.layoutX)
        assertEquals(getPosition().y, adorner.node.layoutY)
        val pane = adorner.node as Pane
        val rect = pane.children.filterIsInstance<Rectangle>().single()
        assertEquals(width, rect.width)
        assertEquals(height, rect.height)
    }

    @Test
    fun `Scale adorner`() {
        val width = 10.0
        val height = 5.0
        val getPosition = { DoublePoint(20.0, 30.0) }
        val scale = doubleProperty(2.0)
        val getScale = { scale.value }
        val adorner = AssociationAdorner(width, height, getPosition, getScale)
        val position = getPosition()
        assertEquals(position.x * scale.value, adorner.node.layoutX)
        assertEquals(position.y * scale.value, adorner.node.layoutY)
        val pane = adorner.node as Pane
        val rect = pane.children.filterIsInstance<Rectangle>().single()
        assertEquals(width * scale.value, rect.width)
        assertEquals(height * scale.value, rect.height)

        scale.value = 0.5
        Thread.sleep(100)
        assertEquals(position.x * scale.value, adorner.node.layoutX)
        assertEquals(position.y * scale.value, adorner.node.layoutY)
        assertEquals(width * scale.value, rect.width)
        assertEquals(height * scale.value, rect.height)
    }

    @Test
    fun `Adorner can be garbage collected after dispose`() {
        val scale = 1.0
        val factory = { AssociationAdorner(100.0, 50.0, { DoublePoint(10.0, 10.0) }, { scale }) }
        testMemoryLeak(factory) { adorner ->
            adorner.destroy()
        }
    }
}
