package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.joml.Vector3f
import solve.constants.ShadersLineLandmarkFragmentPath
import solve.constants.ShadersLineLandmarkVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.plus
import solve.rendering.engine.utils.times
import solve.scene.model.Landmark
import solve.scene.model.Layer.LineLayer
import solve.scene.model.Scene

class LinesLayerRenderer(
    window: Window,
    getScene: () -> Scene?
) : LandmarkLayerRenderer(window, getScene) {
    private var visibleLineLayers = emptyList<LineLayer>()
    private var visibleLineLayersLandmarks = emptyList<List<Landmark.Line>>()

    override val maxBatchSize = 1000

    override fun createShaderProgram(): ShaderProgram {
        val shaderProgram = ShaderProgram()
        shaderProgram.addShader(ShadersLineLandmarkVertexPath, ShaderType.VERTEX)
        shaderProgram.addShader(ShadersLineLandmarkFragmentPath, ShaderType.FRAGMENT)
        shaderProgram.link()

        return shaderProgram
    }

    override fun createNewBatch(zIndex: Int): RenderBatch {
        val shaderAttributesTypes = listOf(
            ShaderAttributeType.FLOAT2,
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
        super.beforeRender()
        visibleLineLayers = visibleLayers.filterIsInstance<LineLayer>()
        visibleLineLayersLandmarks = visibleLayersLandmarks.map { it.filterIsInstance<Landmark.Line>() }
        val firstLineLayer = visibleLayers.filterIsInstance<LineLayer>().firstOrNull() ?: return
        renderPriority = getScene()?.indexOf(firstLineLayer.settings) ?: return
    }

    override fun updateBatchesData() {
        val firstLayer = visibleLineLayers.firstOrNull() ?: return
        if (!firstLayer.settings.enabled) {
            return
        }

        val linesWidth = getLinesWidth()

        visibleLineLayersLandmarks.forEachIndexed { visibleLayerIndex, linesLayerLandmarks ->
            linesLayerLandmarks.forEach { lineLandmark ->
                val selectionLayerIndex = visibleLayersSelectionIndices[visibleLayerIndex]
                val batch = getAvailableBatch(null, 0)

                val lineStartPosition = Vector2f(
                    lineLandmark.startCoordinate.x.toFloat(),
                    lineLandmark.startCoordinate.y.toFloat()
                )
                val lineFinishPosition = Vector2f(
                    lineLandmark.finishCoordinate.x.toFloat(),
                    lineLandmark.finishCoordinate.y.toFloat()
                )
                val lineStartShaderPosition = getFramePixelShaderPosition(selectionLayerIndex, lineStartPosition)
                val lineFinishShaderPosition = getFramePixelShaderPosition(selectionLayerIndex, lineFinishPosition)
                val lineVector = lineFinishShaderPosition - lineStartShaderPosition
                val normalVector = Vector2f(-lineVector.y, lineVector.x).normalize()
                val linePoints = listOf(lineStartShaderPosition, lineFinishShaderPosition)
                val highlightingProgress = lineLandmark.layerState.getLandmarkHighlightingProgress(lineLandmark.uid)

                var widthMultiplier = 1f
                var lineColor = lineLandmark.layerSettings.getColor(lineLandmark)
                if (highlightingProgress == 1f) {
                    widthMultiplier = HighlightingWidthMultiplier
                    lineColor = lineLandmark.layerSettings.getUniqueColor(lineLandmark)
                } else if (highlightingProgress > 0f && highlightingProgress < 1f) {
                    widthMultiplier += highlightingProgress * (HighlightingWidthMultiplier - 1f)
                    lineColor = lineColor.interpolate(
                        lineLandmark.layerSettings.getUniqueColor(lineLandmark),
                        highlightingProgress.toDouble()
                    )
                }

                val lineColorVector = Vector3f(
                    lineColor.red.toFloat(),
                    lineColor.green.toFloat(),
                    lineColor.blue.toFloat()
                )

                linePoints.forEachIndexed { sideIndex, linePoint ->
                    val pointToVertexVector = Vector2f(normalVector) * linesWidth * widthMultiplier /
                        window.camera.zoom / DefaultLocalVerticesPositionsDivider

                    val upperVertexPosition = linePoint + pointToVertexVector
                    val bottomVertexPosition = linePoint - pointToVertexVector
                    val firstVertexPosition = if (sideIndex == 0) upperVertexPosition else bottomVertexPosition
                    val secondVertexPosition = if (sideIndex == 0) bottomVertexPosition else upperVertexPosition
                    batch.pushVector2f(firstVertexPosition)
                    batch.pushVector3f(lineColorVector)
                    batch.pushVector2f(secondVertexPosition)
                    batch.pushVector3f(lineColorVector)
                }
            }
        }
    }

    private fun getLinesWidth(): Float {
        return visibleLineLayers.firstOrNull()?.settings?.selectedWidth?.toFloat() ?: return 1f
    }

    private fun useCommonColor(): Boolean {
        return visibleLineLayers.firstOrNull()?.settings?.useCommonColor ?: false
    }

    private fun getLineWidth(): Float {
        return visibleLineLayers.firstOrNull()?.settings?.selectedWidth?.toFloat() ?: return 1f
    }

    companion object {
        private const val UseCommonColorUniformName = "uUseCommonColor"

        private const val DefaultLocalVerticesPositionsDivider = 800f
        private const val HighlightingWidthMultiplier = 2.5f
    }
}
