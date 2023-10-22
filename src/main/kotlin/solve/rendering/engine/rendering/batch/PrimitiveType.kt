package solve.rendering.engine.rendering.batch

import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_POINTS
import org.lwjgl.opengl.GL11.GL_TRIANGLES

enum class PrimitiveType(
    val verticesNumber: Int,
    val openGLPrimitive: Int,
    val verticesDrawingOrder: List<Int>
) {
    Point(1, GL_POINTS, listOf(0)),
    Line(2, GL_LINES, listOf(0, 1)),
    Triangle(3, GL_TRIANGLES, listOf(0, 1, 2)),
    Quad(4, GL_TRIANGLES, listOf(3, 2, 0, 0, 2, 1));

    val drawingOrderElementsNumber = verticesDrawingOrder.count()
}
