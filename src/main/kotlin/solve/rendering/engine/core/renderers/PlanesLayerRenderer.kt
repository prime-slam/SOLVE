package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import solve.constants.ShadersPlaneLandmarkFragmentPath
import solve.constants.ShadersPlaneLandmarkVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.core.texture.Texture
import solve.rendering.engine.core.texture.Texture2D
import solve.rendering.engine.core.texture.TextureFilterType
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.utils.plus
import solve.scene.model.Layer.PlaneLayer
import solve.scene.model.Scene

class PlanesLayerRenderer(
    window: Window,
    getScene: () -> Scene?
) : LandmarkLayerRenderer(window, getScene) {
    private var visiblePlaneLayers = emptyList<PlaneLayer>()
    private var visiblePlaneLayersTextures = emptyList<Texture2D>()

    override val maxBatchSize = 1000

    private var needToInitializePlaneTextures = false

    override fun createShaderProgram(): ShaderProgram {
        val shaderProgram = ShaderProgram()
        shaderProgram.addShader(ShadersPlaneLandmarkVertexPath, ShaderType.VERTEX)
        shaderProgram.addShader(ShadersPlaneLandmarkFragmentPath, ShaderType.FRAGMENT)
        shaderProgram.link()

        return shaderProgram
    }

    override fun createNewBatch(zIndex: Int): RenderBatch {
        val shaderAttributesTypes = listOf(
            ShaderAttributeType.FLOAT2,
            ShaderAttributeType.FLOAT2,
            ShaderAttributeType.FLOAT
        )

        return RenderBatch(
            maxBatchSize,
            zIndex,
            PrimitiveType.Quad,
            shaderAttributesTypes
        )
    }

    override fun uploadUniforms(shaderProgram: ShaderProgram) {
        shaderProgram.uploadMatrix4f(ProjectionUniformName, window.calculateProjectionMatrix())
        shaderProgram.uploadIntArray(TexturesUniformName, texturesIndices)
    }

    override fun delete() {
        visiblePlaneLayersTextures.forEach { it.delete() }
        super.delete()
    }

    override fun beforeRender() {
        super.beforeRender()
        visiblePlaneLayers = visibleLayers.filterIsInstance<PlaneLayer>()
        if (needToInitializePlaneTextures) {
            visiblePlaneLayersTextures.forEach { it.delete() }
            visiblePlaneLayersTextures = visiblePlaneLayers.map {
                Texture2D(it.filePath.toString(), TextureFilterType.PixelPerfect)
            }
            needToInitializePlaneTextures = false
        }

        val firstPlaneLayer = layers.filterIsInstance<PlaneLayer>().firstOrNull() ?: return
        renderPriority = getScene()?.indexOf(firstPlaneLayer.settings) ?: return
    }

    override fun updateBatchesData() {
        val firstLayer = visiblePlaneLayers.firstOrNull() ?: return
        if (!firstLayer.settings.enabled) {
            return
        }

        visiblePlaneLayers.forEachIndexed { visibleLayerIndex, _ ->
            val selectionLayerIndex = visibleLayersSelectionIndices[visibleLayerIndex]
            val planeLayerTexture = visiblePlaneLayersTextures[visibleLayerIndex]
            val batch = getAvailableBatch(planeLayerTexture, 0)
            val textureID = batch.getTextureLocalID(planeLayerTexture)
            val topLeftFrameShaderPosition = getFrameTopLeftShaderPosition(selectionLayerIndex)
            textureLocalVerticesPositions.forEachIndexed { localVertexIndex, localVertexPosition ->
                val scaledLocalVertexPosition = Vector2f(localVertexPosition.x * framesRatio, localVertexPosition.y)
                val vertexShaderPosition = topLeftFrameShaderPosition + scaledLocalVertexPosition
                batch.pushVector2f(vertexShaderPosition)
                batch.pushVector2f(Texture.defaultUVCoordinates[localVertexIndex])
                batch.pushFloat(textureID.toFloat())
            }
        }
    }
    companion object {
        private const val TexturesUniformName = "uTextures"

        private val texturesIndices = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)

        private val textureLocalVerticesPositions = listOf(
            Vector2f(0.0f, 1.0f),
            Vector2f(0.0f, 0.0f),
            Vector2f(1.0f, 0.0f),
            Vector2f(1.0f, 1.0f)
        )
    }
}
