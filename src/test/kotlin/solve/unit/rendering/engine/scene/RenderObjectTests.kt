package solve.unit.rendering.engine.scene

import org.junit.jupiter.api.Test
import solve.rendering.engine.components.SpriteRenderer
import solve.rendering.engine.scene.RenderObject

internal class RenderObjectTests {
    @Test
    fun `Adds a component to a render object and checks if it contained`() {
        val renderObject = RenderObject("Render object")
        val addedComponent = SpriteRenderer()
        renderObject.addComponent(addedComponent)
        assert(renderObject.hasComponent(addedComponent))
    }

    @Test
    fun `Removes an added component and checks if it does not exists`() {
        val renderObject = RenderObject("Render object")
        val addedComponent = SpriteRenderer()
        renderObject.addComponent(addedComponent)
        renderObject.removeComponent(addedComponent)
        assert(!renderObject.hasComponent(addedComponent))
    }
}
