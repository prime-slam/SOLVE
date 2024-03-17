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

class LinesLayerRenderer(
    window: Window
) : LandmarkLayerRenderer(window) {
    private var lineLayers = emptyList<LineLayer>()
    private var lineLayersLandmarks = emptyList<List<Landmark.Line>>()

    override val maxBatchSize = 1000

    private var lineWidth = 1f

    override fun setNewLayers(layers: List<Layer>, framesSize: Vector2f) {
        super.setNewLayers(lineLayers, framesSize)
        val lineLayers = layers.filterIsInstance<LineLayer>()
        this.lineLayers = lineLayers
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
            ShaderAttributeType.FLOAT2
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

    override fun updateBatchesData() {
        lineLayersLandmarks.forEachIndexed { linesLayerIndex, linesLayerLandmarks ->
            linesLayerLandmarks.forEach { lineLandmark ->
                val batch = getAvailableBatch(null, 0)

                val lineStartPosition = Vector2f(
                    lineLandmark.startCoordinate.x.toFloat(),
                    lineLandmark.startCoordinate.y.toFloat()
                )
                val lineFinishPosition = Vector2f(
                    lineLandmark.finishCoordinate.x.toFloat(),
                    lineLandmark.finishCoordinate.y.toFloat()
                )
                val lineStartShaderPosition = framePixelToShaderPosition(linesLayerIndex, lineStartPosition)
                val lineFinishShaderPosition = framePixelToShaderPosition(linesLayerIndex, lineFinishPosition)
                val lineVector = lineFinishShaderPosition - lineStartShaderPosition
                val normalVector = Vector2f(-lineVector.y, lineVector.x).normalize()
                val linePoints = listOf(lineStartShaderPosition, lineFinishShaderPosition)

                linePoints.forEach { linePoint ->
                    val pointToVertexVector = Vector2f(normalVector) *
                        lineWidth / window.camera.zoom / DefaultLocalVerticesPositionsDivider
                    val linePointFirstVertexPosition = linePoint + pointToVertexVector
                    val linePointSecondVertexPosition = linePoint - pointToVertexVector
                    batch.pushVector2f(linePointFirstVertexPosition)
                    batch.pushVector2f(linePointSecondVertexPosition)
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

    private fun useCommonColor(): Boolean {
        return lineLayers.firstOrNull()?.settings?.useCommonColor ?: false
    }

    private fun getLineWidth(): Float {
        return lineLayers.firstOrNull()?.settings?.selectedWidth?.toFloat() ?: return 1f
    }

    companion object {
        private const val ProjectionUniformName = "uProjection"

        private val circleBoundsVerticesLocalPositions = listOf(
            Vector2f(1f, 1f),
            Vector2f(1f, -1f),
            Vector2f(-1f, -1f),
            Vector2f(-1f, 1f)
        )

        private const val DefaultLocalVerticesPositionsDivider = 250f
    }
}
