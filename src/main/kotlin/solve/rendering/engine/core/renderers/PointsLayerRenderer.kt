package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.joml.Vector3f
import solve.constants.ShadersPointLandmarkFragmentPath
import solve.constants.ShadersPointLandmarkVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.utils.plus
import solve.rendering.engine.utils.times
import solve.scene.model.Landmark
import solve.scene.model.Layer
import solve.scene.model.Layer.PointLayer
import solve.scene.model.Scene

class PointsLayerRenderer(
    window: Window,
    getScene: () -> Scene?
) : LandmarkLayerRenderer(window, getScene) {
    private var pointLayers = emptyList<PointLayer>()
    private var pointLayersLandmarks = emptyList<List<Landmark.Keypoint>>()

    override val maxBatchSize = 1000

    override fun setFramesSelectionLayers(layers: List<Layer>) {
        pointLayers = layers.filterIsInstance<PointLayer>()
        pointLayersLandmarks = pointLayers.map { it.getLandmarks() }
    }

    override fun createShaderProgram(): ShaderProgram {
        val shaderProgram = ShaderProgram()
        shaderProgram.addShader(ShadersPointLandmarkVertexPath, ShaderType.VERTEX)
        shaderProgram.addShader(ShadersPointLandmarkFragmentPath, ShaderType.FRAGMENT)
        shaderProgram.link()

        return shaderProgram
    }

    override fun createNewBatch(zIndex: Int): RenderBatch {
        val shaderAttributesTypes = listOf(
            ShaderAttributeType.FLOAT2,
            ShaderAttributeType.FLOAT2,
            ShaderAttributeType.FLOAT,
            ShaderAttributeType.FLOAT3
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
        shaderProgram.uploadInt(UseCommonColorUniformName, if (useCommonColor()) 1 else 0)
    }

    override fun beforeRender() {
        val firstPlaneLayer = pointLayers.firstOrNull() ?: return
        renderPriority = getScene()?.indexOf(firstPlaneLayer.settings) ?: return
    }

    override fun updateBatchesData() {
        val firstLayer = pointLayers.firstOrNull() ?: return
        if (!firstLayer.settings.enabled) {
            return
        }

        val pointsRadius = getPointsRadius()

        pointLayersLandmarks.forEachIndexed { pointsLayerIndex, pointsLayerLandmarks ->
            pointsLayerLandmarks.forEachIndexed { pointLandmarkIndex, pointLandmark ->
                val batch = getAvailableBatch(null, 0)

                val pointLandmarkPosition = Vector2f(
                    pointLandmark.coordinate.x.toFloat(),
                    pointLandmark.coordinate.y.toFloat()
                )
                val pointShaderPosition = getFramePixelShaderPosition(pointsLayerIndex, pointLandmarkPosition)

                val highlightingProgress = pointLandmark.layerState.getLandmarkHighlightingProgress(pointLandmark.uid)

                var radiusMultiplier = 1f
                var pointColor = pointLandmark.layerSettings.getColor(pointLandmark)
                if (highlightingProgress == 1f) {
                    radiusMultiplier = HighlightingRadiusMultiplier
                    pointColor = pointLandmark.layerSettings.getUniqueColor(pointLandmark)
                } else if (highlightingProgress > 0f && highlightingProgress < 1f ) {
                    radiusMultiplier += highlightingProgress * (HighlightingRadiusMultiplier - 1f)
                    pointColor = pointColor.interpolate(
                        pointLandmark.layerSettings.getUniqueColor(pointLandmark),
                        highlightingProgress.toDouble()
                    )
                }

                val pointColorVector = Vector3f(
                    pointColor.red.toFloat(),
                    pointColor.green.toFloat(),
                    pointColor.blue.toFloat()
                )

                circleBoundsVerticesLocalPositions.forEach { vertexLocalPosition ->
                    val vertexPosition = pointShaderPosition + Vector2f(vertexLocalPosition) *
                        pointsRadius * radiusMultiplier / window.camera.zoom / DefaultLocalVerticesPositionsDivider
                    batch.pushVector2f(vertexPosition)
                    batch.pushVector2f(vertexLocalPosition)
                    batch.pushFloat(pointLandmarkIndex.toFloat())
                    batch.pushVector3f(pointColorVector)
                }
            }
        }
    }

    private fun useCommonColor(): Boolean {
        return pointLayers.firstOrNull()?.settings?.useCommonColor ?: false
    }

    private fun getPointsRadius(): Float {
        return pointLayers.firstOrNull()?.settings?.selectedRadius?.toFloat() ?: return 1f
    }

    companion object {
        private const val UseCommonColorUniformName = "uUseCommonColor"

        private val circleBoundsVerticesLocalPositions = listOf(
            Vector2f(1f, 1f),
            Vector2f(1f, -1f),
            Vector2f(-1f, -1f),
            Vector2f(-1f, 1f)
        )

        private const val DefaultLocalVerticesPositionsDivider = 500f
        private const val HighlightingRadiusMultiplier = 2f
    }
}
