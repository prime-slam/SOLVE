package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import solve.constants.ShadersPlaneLandmarkFragmentPath
import solve.constants.ShadersPlaneLandmarkVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.core.texture.Texture
import solve.rendering.engine.core.texture.Texture2D
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.utils.plus
import solve.scene.model.Layer
import solve.scene.model.Layer.PlaneLayer
import solve.scene.model.Scene

class PlanesLayerRenderer(
    window: Window,
    getScene: () -> Scene?
) : LandmarkLayerRenderer(window, getScene) {
    private var planeLayers = emptyList<PlaneLayer>()
    private var planeLayersTextures = emptyList<Texture2D>()

    override val maxBatchSize = 1000

    private var needToInitializePlaneTextures = false

    override fun setFramesSelectionLayers(layers: List<Layer>) {
        planeLayers = layers.filterIsInstance<PlaneLayer>()
        needToInitializePlaneTextures = true
    }

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
    }

    override fun beforeRender() {

    }

    override fun updateBatchesData() {
        if (needToInitializePlaneTextures) {
            planeLayersTextures = planeLayers.map { Texture2D(it.filePath.toString()) }
            needToInitializePlaneTextures = false
        }

        planeLayers.forEachIndexed { planeLayerIndex, planeLayer ->
            val planeLayerTexture = planeLayersTextures[planeLayerIndex]
            //val layerIndex = getScene()?.indexOf(planeLayer.settings) ?: return@forEachIndexed
            val batch = getAvailableBatch(planeLayerTexture, 0)
            val textureID = batch.getTextureLocalID(planeLayerTexture)
            val topLeftFrameShaderPosition = getFrameTopLeftShaderPosition(planeLayerIndex)
            textureLocalVerticesPositions.forEachIndexed { localVertexIndex, localVertexPosition ->
                val q = Vector2f(localVertexPosition)
                if (planeLayer.name.contains("alg1"))
                    q.mul(0.5f)
                else
                    q.mul(0.8f)
                val scaledLocalVertexPosition = Vector2f(q.x * framesRatio, q.y)
                val vertexShaderPosition = topLeftFrameShaderPosition + scaledLocalVertexPosition
                batch.pushVector2f(vertexShaderPosition)
                batch.pushVector2f(Texture.defaultUVCoordinates[localVertexIndex])
                batch.pushFloat(textureID.toFloat())
            }
        }
    }
    companion object {
        private const val ProjectionUniformName = "uProjection"

        private val textureLocalVerticesPositions = listOf(
            Vector2f(0.0f, 1.0f),
            Vector2f(0.0f, 0.0f),
            Vector2f(1.0f, 0.0f),
            Vector2f(1.0f, 1.0f)
        )
    }
}
