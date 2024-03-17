package solve.rendering.engine.core.renderers

import org.joml.Vector2f
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

class PointsLayerRenderer(
    window: Window
) : LandmarkLayerRenderer(window) {
    private var pointLayers = emptyList<PointLayer>()
    private var pointLayersLandmarks = emptyList<List<Landmark.Keypoint>>()

    override val maxBatchSize = 1000

    private var pointsRadius = 1f

    fun setPointsRadius(pointsRadius: Float) {
        this.pointsRadius = pointsRadius
    }

    fun setNewLayers(pointLayers: List<PointLayer>, framesSize: Vector2f) {
        initializeFrameSizeData(framesSize)
        this.pointLayers = pointLayers
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
        pointLayersLandmarks.forEachIndexed { pointsLayerIndex, pointsLayerLandmarks ->
            pointsLayerLandmarks.forEach { pointLandmark ->
                val batch = getAvailableBatch(null, 0)

                val pointLandmarkPosition = Vector2f(
                    pointLandmark.coordinate.x.toFloat(),
                    pointLandmark.coordinate.y.toFloat()
                )
                val pointShaderPosition = framePixelToShaderPosition(pointsLayerIndex, pointLandmarkPosition)

                circleBoundsVerticesLocalPositions.forEach { vertexLocalPosition ->
                    val vertexPosition = pointShaderPosition +
                            Vector2f(vertexLocalPosition) * pointsRadius / window.camera.zoom / DefaultLocalVerticesPositionsDivider
                    batch.pushVector2f(vertexPosition)
                    batch.pushVector2f(vertexLocalPosition)
                }
            }
        }
    }

    companion object {
        private const val ProjectionUniformName = "uProjection"

        private val circleBoundsVerticesLocalPositions = listOf(
            Vector2f(1f, 1f),
            Vector2f(1f, -1f),
            Vector2f(-1f, -1f),
            Vector2f(-1f, 1f)
        )

        private const val DefaultLocalVerticesPositionsDivider = 70f
    }
}
