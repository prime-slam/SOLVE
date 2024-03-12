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
import solve.scene.model.Landmark
import solve.scene.model.Layer.PointLayer

class PointsLayerRenderer(
    window: Window
) : Renderer(window) {
    private var pointLayers = emptyList<PointLayer>()
    private var pointLayersLandmarks = emptyList<List<Landmark.Keypoint>>()

    override val maxBatchSize = 1000

    private var gridWidth = 6//FramesRenderer.DefaultGridWidth
    private var pointsRadius = 1f
    private var framesSize = Vector2f()

    fun setNewLayers(pointLayers: List<PointLayer>, framesSize: Vector2f) {
        this.pointLayers = pointLayers
        this.framesSize = framesSize
        pointLayersLandmarks = pointLayers.map { it.getLandmarks() }
    }

    fun setGridWidth(gridWidth: Int) {
        if (gridWidth < 1) {
            println("The width of the frames grid should be a positive value!")
            return
        }

        this.gridWidth = gridWidth
    }

    fun setPointsRadius(pointsRadius: Float) {
        this.pointsRadius = pointsRadius
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
        val framesRatio = framesSize.x / framesSize.y

        pointLayersLandmarks.forEachIndexed { pointsLayerIndex, pointsLayerLandmarks ->
            pointsLayerLandmarks.forEach { pointLandmark ->
                val batch = getAvailableBatch(null, 0)

                val frameRelativePosition = Vector2f(
                    pointLandmark.coordinate.x.toFloat() / framesSize.y,
                    pointLandmark.coordinate.y.toFloat() / framesSize.y
                )
                val previousFramesVector = Vector2f(
                    (pointsLayerIndex % gridWidth).toFloat() * framesRatio,
                    (pointsLayerIndex / gridWidth).toFloat()
                )
                val framePosition = previousFramesVector + frameRelativePosition

                circleBoundsVerticesLocalPositions.forEach { vertexLocalPosition ->
                    val vertexPosition =
                        framePosition + Vector2f(vertexLocalPosition) / DefaultLocalVerticesPositionsDivider
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