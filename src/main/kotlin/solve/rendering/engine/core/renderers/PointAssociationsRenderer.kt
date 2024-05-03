package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.joml.Vector3f
import solve.constants.ShadersPointAssociationFragmentPath
import solve.constants.ShadersPointAssociationVertexPath
import solve.rendering.engine.Window
import solve.rendering.engine.core.batch.PrimitiveType
import solve.rendering.engine.core.batch.RenderBatch
import solve.rendering.engine.shader.ShaderAttributeType
import solve.rendering.engine.shader.ShaderProgram
import solve.rendering.engine.shader.ShaderType
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.plus
import solve.rendering.engine.utils.times
import solve.rendering.engine.utils.toFloatVector
import solve.rendering.engine.utils.toVector2i
import solve.scene.view.association.AssociationManager

class PointAssociationsRenderer(
    window: Window,
    private val associationManager: AssociationManager
) : Renderer(window) {
    override val maxBatchSize = 1000

    init {
        renderPriority = 1
    }

    override fun createShaderProgram(): ShaderProgram {
        val shaderProgram = ShaderProgram()
        shaderProgram.addShader(ShadersPointAssociationVertexPath, ShaderType.VERTEX)
        shaderProgram.addShader(ShadersPointAssociationFragmentPath, ShaderType.FRAGMENT)
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
    }

    override fun updateBatchesData() {
        val associationConnections = associationManager.associationConnections

        associationConnections.forEach { associationConnection ->
            associationConnection.associationLines.forEach { associationLine ->
                val batch = getAvailableBatch(null, 0)

                val lineStartShaderPosition = getFramePixelShaderPosition(
                    associationLine.firstKeypointFrameIndex,
                    associationLine.firstKeypoint.coordinate.toVector2i().toFloatVector()
                )
                val lineFinishShaderPosition = getFramePixelShaderPosition(
                    associationLine.secondKeypointFrameIndex,
                    associationLine.secondKeypoint.coordinate.toVector2i().toFloatVector()
                )

                println("line start shader pos: ${lineStartShaderPosition}")
                println("line finish shader pos: ${lineFinishShaderPosition}")

                val lineVector = lineFinishShaderPosition - lineStartShaderPosition
                val normalVector = Vector2f(-lineVector.y, lineVector.x).normalize()
                val linePoints = listOf(lineStartShaderPosition, lineFinishShaderPosition)

                val firstKeypointColor =
                    associationLine.firstKeypoint.layerSettings.getColor(associationLine.firstKeypoint)
                val secondKeypointColor =
                    associationLine.secondKeypoint.layerSettings.getColor(associationLine.secondKeypoint)
                val lineColor = firstKeypointColor.interpolate(secondKeypointColor, 0.5)

                val lineColorVector = Vector3f(
                    lineColor.red.toFloat(),
                    lineColor.green.toFloat(),
                    lineColor.blue.toFloat()
                )

                linePoints.forEachIndexed { sideIndex, linePoint ->
                    val pointToVertexVector = Vector2f(normalVector) * AssociationLineWidth / window.camera.zoom /
                            DefaultLocalVerticesPositionsDivider

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

    companion object {
        private const val DefaultLocalVerticesPositionsDivider = 800f

        private const val AssociationLineWidth = 3.0f
    }
}
