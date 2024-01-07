package solve.unit.rendering.engine.core.batch

import org.joml.Vector3f
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import solve.constants.IconsCatalogueApplyPath
import solve.interactive.InteractiveTestClass
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.core.texture.Texture2D
import solve.rendering.engine.shader.ShaderAttributeType
import solve.unit.rendering.utils.runInOpenGLContext

@ExtendWith(ApplicationExtension::class)
internal class RenderBatchTest {
    @Test
    fun `Fully fills the vertices data buffer`() {
        runInOpenGLContext {
            val testRenderBatch = createTestRenderBatch()
            assert(!testRenderBatch.isFull)
            for (i in 0 until TestRenderBatchSize) {
                testRenderBatch.pushInt(1)
                testRenderBatch.pushFloat(2f)
                testRenderBatch.pushVector3f(Vector3f(3f, 4f, 5f))

                if (i < TestRenderBatchSize - 1) {
                    assert(!testRenderBatch.isFull)
                }
            }
            assert(testRenderBatch.isFull)
        }
    }

    @Test
    fun `Fully fills the textures slots`() {
        runInOpenGLContext {
            repeat(MaxTexturesNumber) {
                val testRenderBatch = createTestRenderBatch()
                assert(!testRenderBatch.isTexturesFull)
                for (i in 0 until MaxTexturesNumber) {
                    testRenderBatch.pushInt(1)
                    testRenderBatch.pushFloat(2f)
                    testRenderBatch.pushVector3f(Vector3f(3f, 4f, 5f))

                    if (i < MaxTexturesNumber - 1) {
                        assert(!testRenderBatch.isTexturesFull)
                    }
                }
                assert(testRenderBatch.isFull)
            }
        }
    }

    @Test
    fun `Adds new texture and checks if it is contained`() {
        runInOpenGLContext {
            val addingTexture = createTestTexture2D()
            val testRenderBatch = createTestRenderBatch()
            testRenderBatch.addTexture(addingTexture)
            assert(testRenderBatch.containsTexture(addingTexture))
        }
    }

    @Test
    fun `Removes added texture and checks if it does not contained`() {
        runInOpenGLContext {
            val texture = createTestTexture2D()
            val testRenderBatch = createTestRenderBatch()
            testRenderBatch.addTexture(texture)
            testRenderBatch.removeTexture(texture)
            assert(!testRenderBatch.containsTexture(texture))
        }
    }

    @Test
    fun `Adds some vertex data and checks if vertices counting is correct`() {
        runInOpenGLContext {
            val testRenderBatch = createTestRenderBatch()
            val verticesNumber = 3
            for (i in 0 until verticesNumber) {
                testRenderBatch.pushInt(2)
                testRenderBatch.pushFloat(3f)
                testRenderBatch.pushVector3f(Vector3f(4f, 5f, 6f))
            }
            assertEquals(verticesNumber, testRenderBatch.getVerticesNumber())
        }
    }

    companion object {
        private const val TestRenderBatchSize = 10
        private const val MaxTexturesNumber = 8

        @JvmStatic
        @BeforeAll
        fun setSystemProperties() {
            System.setProperty("testfx.headless", "true")
            System.setProperty("prism.order", "sw")
            System.setProperty("prism.text", "t2k")
        }

        private fun createTestRenderBatch() = RenderBatch(
            TestRenderBatchSize,
            -1,
            PrimitiveType.Triangle,
            listOf(
                ShaderAttributeType.INT,
                ShaderAttributeType.FLOAT,
                ShaderAttributeType.FLOAT3
            )
        )

        private fun createTestTexture2D() = Texture2D(IconsCatalogueApplyPath)
    }
}
