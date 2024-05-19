package solve.rendering.engine.core.renderers

import org.joml.Vector2f
import org.joml.Vector4f
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
import kotlin.math.min

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
    }

    override fun updateBatchesData() {
        val associationConnections = associationManager.associationConnections

        associationConnections.forEach { associationConnection ->
            associationConnection.associationLines.forEach { associationLine ->
                val lineStartShaderPosition = getFramePixelShaderPosition(
                    associationLine.firstKeypointFrameIndex,
                    associationLine.firstKeypoint.coordinate.toVector2i().toFloatVector()
                )
                val lineFinishShaderPosition = getFramePixelShaderPosition(
                    associationLine.secondKeypointFrameIndex,
                    associationLine.secondKeypoint.coordinate.toVector2i().toFloatVector()
                )

                val lineVector = lineFinishShaderPosition - lineStartShaderPosition
                val normalVector = Vector2f(-lineVector.y, lineVector.x).normalize()

                val firstKeypointColor =
                    associationLine.firstKeypoint.layerSettings.getColor(associationLine.firstKeypoint)
                val secondKeypointColor =
                    associationLine.secondKeypoint.layerSettings.getColor(associationLine.secondKeypoint)
                val lineColor = firstKeypointColor.interpolate(secondKeypointColor, 0.5)

                val lineColorVector = Vector4f(
                    lineColor.red.toFloat(),
                    lineColor.green.toFloat(),
                    lineColor.blue.toFloat(),
                    1f
                )
                val zeroAlphaLineColorVector = Vector4f(lineColorVector).also { it.w = 0f }

                drawLineRectVertices(
                    lineStartShaderPosition,
                    lineFinishShaderPosition,
                    AssociationLineWidth,
                    lineColorVector,
                    lineColorVector,
                    normalVector
                )
                val nonOpaqueLineRectWidth = min(
                    AssociationLineWidth * NonOpaqueLineWidthFactor,
                    NonOpaqueMaxLineWidth
                )
                val nonOpaqueRectNormalOffset = Vector2f(normalVector) *
                    (AssociationLineWidth + nonOpaqueLineRectWidth) / 2f / window.camera.zoom /
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

    companion object {
        private const val DefaultLocalVerticesPositionsDivider = 800f

        private const val AssociationLineWidth = 3f
        private const val NonOpaqueLineWidthFactor = 1f
        private const val NonOpaqueMaxLineWidth = 3f
    }
}
