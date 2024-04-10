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
import solve.scene.model.Layer
import solve.scene.model.Layer.LineLayer
import solve.scene.model.Scene

class LinesLayerRenderer(
    window: Window,
    getScene: () -> Scene?
) : LandmarkLayerRenderer(window, getScene) {
    private var lineLayers = emptyList<LineLayer>()
    private var lineLayersLandmarks = emptyList<List<Landmark.Line>>()

    override val maxBatchSize = 1000

    override fun setFramesSelectionLayers(layers: List<Layer>) {
        lineLayers = layers.filterIsInstance<LineLayer>()
        lineLayersLandmarks = lineLayers.map { it.getLandmarks() }
    }

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
        shaderProgram.uploadInt(UseCommonColorUniformName, if (useCommonColor()) 1 else 0)
        shaderProgram.uploadVector3f(CommonColorUniformName, getLinesColor())
    }

    override fun beforeRender() {
        val firstPlaneLayer = lineLayers.firstOrNull() ?: return
        renderPriority = getScene()?.indexOf(firstPlaneLayer.settings) ?: return
    }

    override fun updateBatchesData() {
        val firstLayer = lineLayers.firstOrNull() ?: return
        if (!firstLayer.settings.enabled) {
            return
        }

        val linesWidth = getLinesWidth()

        lineLayersLandmarks.forEachIndexed { linesLayerIndex, linesLayerLandmarks ->
            linesLayerLandmarks.forEachIndexed { lineLandmarkIndex, lineLandmark ->
                val batch = getAvailableBatch(null, 0)

                val lineStartPosition = Vector2f(
                    lineLandmark.startCoordinate.x.toFloat(),
                    lineLandmark.startCoordinate.y.toFloat()
                )
                val lineFinishPosition = Vector2f(
                    lineLandmark.finishCoordinate.x.toFloat(),
                    lineLandmark.finishCoordinate.y.toFloat()
                )
                val lineStartShaderPosition = getFramePixelShaderPosition(linesLayerIndex, lineStartPosition)
                val lineFinishShaderPosition = getFramePixelShaderPosition(linesLayerIndex, lineFinishPosition)
                val lineVector = lineFinishShaderPosition - lineStartShaderPosition
                val normalVector = Vector2f(-lineVector.y, lineVector.x).normalize()
                val linePoints = listOf(lineStartShaderPosition, lineFinishShaderPosition)

                linePoints.forEachIndexed { sideIndex, linePoint ->
                    val pointToVertexVector = Vector2f(normalVector) *
                        linesWidth / window.camera.zoom / DefaultLocalVerticesPositionsDivider

                    val upperVertexPosition = linePoint + pointToVertexVector
                    val bottomVertexPosition = linePoint - pointToVertexVector
                    val firstVertexPosition = if (sideIndex == 0) upperVertexPosition else bottomVertexPosition
                    val secondVertexPosition = if (sideIndex == 0) bottomVertexPosition else upperVertexPosition
                    batch.pushVector2f(firstVertexPosition)
                    batch.pushFloat(lineLandmarkIndex.toFloat())
                    batch.pushVector2f(secondVertexPosition)
                    batch.pushFloat(lineLandmarkIndex.toFloat())
                }
            }
        }
    }

    private fun getLinesColor(): Vector3f {
        val pointsCommonColor = lineLayers.firstOrNull()?.settings?.commonColor ?: return Vector3f(1f, 0f, 0f)

        return Vector3f(
            pointsCommonColor.red.toFloat(),
            pointsCommonColor.green.toFloat(),
            pointsCommonColor.blue.toFloat()
        )
    }

    private fun getLinesWidth(): Float {
        return lineLayers.firstOrNull()?.settings?.selectedWidth?.toFloat() ?: return 1f
    }

    private fun useCommonColor(): Boolean {
        return lineLayers.firstOrNull()?.settings?.useCommonColor ?: false
    }

    private fun getLineWidth(): Float {
        return lineLayers.firstOrNull()?.settings?.selectedWidth?.toFloat() ?: return 1f
    }

    companion object {
        private const val UseCommonColorUniformName = "uUseCommonColor"
        private const val CommonColorUniformName = "uCommonColor"

        private const val DefaultLocalVerticesPositionsDivider = 800f
    }
}
