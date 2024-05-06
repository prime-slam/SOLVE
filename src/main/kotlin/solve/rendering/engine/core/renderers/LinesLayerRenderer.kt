package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.joml.Vector4f
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
import kotlin.math.min

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
            ShaderAttributeType.FLOAT4
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

                val lineColorVector = Vector4f(
                    lineColor.red.toFloat(),
                    lineColor.green.toFloat(),
                    lineColor.blue.toFloat(),
                    1f
                )
                val zeroAlphaLineColorVector = Vector4f(lineColorVector).also { it.w = 0f }

                val opaqueLineRectWidth = linesWidth * widthMultiplier
                drawLineRectVertices(
                    lineStartShaderPosition,
                    lineFinishShaderPosition,
                    opaqueLineRectWidth,
                    lineColorVector,
                    lineColorVector,
                    normalVector
                )
                val nonOpaqueLineRectWidth = min(
                    opaqueLineRectWidth * NonOpaqueLineWidthFactor,
                    NonOpaqueMaxLineWidth
                )
                val nonOpaqueRectNormalOffset = Vector2f(normalVector) *
                    (opaqueLineRectWidth + nonOpaqueLineRectWidth) / 2f / window.camera.zoom /
                    DefaultLocalVerticesPositionsDivider
                drawLineRectVertices(
                    lineStartShaderPosition + nonOpaqueRectNormalOffset,
                    lineFinishShaderPosition + nonOpaqueRectNormalOffset,
                    nonOpaqueLineRectWidth,
                    lineColorVector,
                    zeroAlphaLineColorVector,
                    normalVector
                )
                drawLineRectVertices(
                    lineStartShaderPosition - nonOpaqueRectNormalOffset,
                    lineFinishShaderPosition - nonOpaqueRectNormalOffset,
                    nonOpaqueLineRectWidth,
                    zeroAlphaLineColorVector,
                    lineColorVector,
                    normalVector
                )
            }
        }
    }

    private fun drawLineRectVertices(
        rectCenterStartPoint: Vector2f,
        rectCenterFinishPoint: Vector2f,
        rectWidth: Float,
        bottomVerticesColor: Vector4f,
        upperVerticesColor: Vector4f,
        normalVector: Vector2f
    ) {
        val batch = getAvailableBatch(null, 0)
        val centerLinePoints = listOf(rectCenterStartPoint, rectCenterFinishPoint)
        centerLinePoints.forEachIndexed { sideIndex, linePoint ->
            val pointToVertexVector = Vector2f(normalVector) * (rectWidth / 2f) / window.camera.zoom /
                DefaultLocalVerticesPositionsDivider

            val upperVertexPosition = linePoint + pointToVertexVector
            val bottomVertexPosition = linePoint - pointToVertexVector
            val firstVertexPosition = if (sideIndex == 0) upperVertexPosition else bottomVertexPosition
            val secondVertexPosition = if (sideIndex == 0) bottomVertexPosition else upperVertexPosition
            val firstVertexColor = if (sideIndex == 0) upperVerticesColor else bottomVerticesColor
            val secondVertexColor = if (sideIndex == 0) bottomVerticesColor else upperVerticesColor
            batch.pushVector2f(firstVertexPosition)
            batch.pushVector4f(firstVertexColor)
            batch.pushVector2f(secondVertexPosition)
            batch.pushVector4f(secondVertexColor)
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

        private val boundsVerticesLocalPositions = listOf(
            Vector2f(1f, 1f),
            Vector2f(1f, -1f),
            Vector2f(-1f, -1f),
            Vector2f(-1f, 1f)
        )
        private const val DefaultLocalVerticesPositionsDivider = 600f
        private const val HighlightingWidthMultiplier = 2.5f
        private const val NonOpaqueLineWidthFactor = 1f
        private const val NonOpaqueMaxLineWidth = 4f
    }
}
