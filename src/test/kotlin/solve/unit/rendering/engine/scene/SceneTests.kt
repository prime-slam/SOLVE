package solve.unit.rendering.engine.scene

import org.junit.jupiter.api.Test
import solve.rendering.engine.scene.RenderObject
import solve.rendering.engine.scene.Scene

class SceneTests {
    @Test
    fun `Adds a render object to a scene and checks if it contained`() {
        val scene = Scene()
        val addedRenderObject = RenderObject("Render object")
        scene.addRenderObject(addedRenderObject)
        assert(scene.renderObjects.contains(addedRenderObject))
    }

    @Test
    fun `Removes added render object and checks if it does not exists`() {
        val scene = Scene()
        val addedRenderObject = RenderObject("Render object")
        scene.addRenderObject(addedRenderObject)
        scene.removeRenderObject(addedRenderObject)
        assert(!scene.renderObjects.contains(addedRenderObject))
    }
}
