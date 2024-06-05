package solve.unit.rendering.engine.core.batch

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import solve.constants.IconsCatalogueApplyPath
import solve.interactive.InteractiveTestClass
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.core.texture.Texture2D
import solve.rendering.utils.runInOpenGLContext
import solve.utils.getResourceAbsolutePath

@ExtendWith(ApplicationExtension::class)
internal class RenderBatchTests : InteractiveTestClass() {
    @Test
    fun `Fully fills a render batch with textures`() {
        runInOpenGLContext {
            val renderBatch = RenderBatch(1000, 0, PrimitiveType.Quad, emptyList())
            assertEquals(false, renderBatch.isTexturesFull)
            repeat(RenderBatch.MaxTexturesNumber - 1) {
                renderBatch.addTexture(createTestTexture())
                assertEquals(false, renderBatch.isTexturesFull)
            }
            renderBatch.addTexture(createTestTexture())
            assertEquals(true, renderBatch.isTexturesFull)
        }
    }

    companion object {
        fun createTestTexture(): Texture2D {
            return Texture2D(getResourceAbsolutePath(IconsCatalogueApplyPath).toString())
        }
    }
}
