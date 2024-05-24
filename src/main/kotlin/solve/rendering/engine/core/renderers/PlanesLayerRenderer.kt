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
import solve.scene.model.Layer.PlanesLayer
import solve.scene.model.LayerState
import solve.scene.model.Scene

class PlanesLayerRenderer(
    window: Window,
    getScene: () -> Scene?
) : LandmarkLayerRenderer(window, getScene) {
    private var visiblePlanesLayers = emptyList<PlanesLayer>()
    private var visiblePlanesLayersToTexturesMap = mutableMapOf<PlanesLayer, Texture2D>()
    private var visiblePlaneLayersTextures = listOf<Texture2D>()

    private val planeLayersState: LayerState?
        get() = layers.filterIsInstance<PlanesLayer>().firstOrNull()?.layerState

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

        val layersState = planeLayersState
        var interactingPlanesNumber = 0
        if (layersState != null) {
            val interactingPlanesUIDs = layersState.selectedLandmarksUIDs.union(
                layersState.hoveredLandmarkUIDs
            ).distinct()
            shaderProgram.uploadIntArray(
                InteractingPlanesUIDsUniformName,
                interactingPlanesUIDs.map { it.toInt() }.toIntArray()
            )
            val planesOpacity = interactingPlanesUIDs.map {
                1f - layersState.getLandmarkHighlightingProgress(it)
            }.toFloatArray()
            shaderProgram.uploadFloatArray(InteractingPlanesOpacityUniformName, planesOpacity)
            interactingPlanesNumber = interactingPlanesUIDs.count()
        }
        shaderProgram.uploadInt(InteractingPlanesNumberUniformName, interactingPlanesNumber)
    }

    override fun delete() {
        visiblePlanesLayersToTexturesMap.values.forEach { it.delete() }
        visiblePlanesLayersToTexturesMap.clear()
        super.delete()
    }

    override fun beforeRender() {
        super.beforeRender()
        visiblePlanesLayers = visibleLayers.filterIsInstance<PlanesLayer>()

        val hiddenVisiblePlanesLayers = hiddenVisibleLayersInCurrentFrame.filterIsInstance<PlanesLayer>()
        val newVisiblePlanesLayers = newVisibleLayersInCurrentFrame.filterIsInstance<PlanesLayer>()

        hiddenVisiblePlanesLayers.forEach {
            visiblePlanesLayersToTexturesMap[it]?.delete()
            visiblePlanesLayersToTexturesMap.remove(it)
        }
        newVisiblePlanesLayers.forEach {
            visiblePlanesLayersToTexturesMap[it] = Texture2D(it.filePath.toString(), TextureFilterType.PixelPerfect)
        }
        visiblePlaneLayersTextures = visiblePlanesLayersToTexturesMap.keys.sortedBy {
            visiblePlanesLayers.indexOf(it)
        }.mapNotNull { visiblePlanesLayersToTexturesMap[it] }

        val firstPlanesLayer = layers.filterIsInstance<PlanesLayer>().firstOrNull() ?: return
        renderPriority = getScene()?.indexOf(firstPlanesLayer.settings) ?: return
    }

    override fun updateBatchesData() {
        val firstLayer = visiblePlanesLayers.firstOrNull() ?: return
        if (!firstLayer.settings.enabled) {
            return
        }

        visiblePlanesLayers.forEachIndexed { visibleLayerIndex, _ ->
            if (visibleLayerIndex !in visibleLayersSelectionIndices.indices ||
                visibleLayerIndex !in visiblePlaneLayersTextures.indices)
                return

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
        private const val InteractingPlanesUIDsUniformName = "uInteractingPlanesUIDs"
        private const val InteractingPlanesOpacityUniformName = "uInteractingPlanesOpacity"
        private const val InteractingPlanesNumberUniformName = "uInteractingPlanesNumber"

        private val texturesIndices = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)

        private val textureLocalVerticesPositions = listOf(
            Vector2f(0.0f, 1.0f),
            Vector2f(0.0f, 0.0f),
            Vector2f(1.0f, 0.0f),
            Vector2f(1.0f, 1.0f)
        )
    }
}
