package solve.scene.view.utils

import javafx.animation.*
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.util.Duration

fun createScaleTransition(node: Node, newScaleX: Double, newScaleY: Double, duration: Duration): Transition {
    val scaleTransition = ScaleTransition()
    scaleTransition.duration = duration
    scaleTransition.toX = newScaleX
    scaleTransition.toY = newScaleY
    scaleTransition.node = node
    return scaleTransition
}

fun createFillTransition(shape: Shape, color: Color, duration: Duration): Transition {
    val fillTransition = FillTransition()
    fillTransition.duration = duration
    fillTransition.toValue = color
    fillTransition.shape = shape
    return fillTransition
}

fun createStrokeTransition(shape: Shape, color: Color, duration: Duration): Transition {
    val strokeTransition = StrokeTransition()
    strokeTransition.duration = duration
    strokeTransition.toValue = color
    strokeTransition.shape = shape
    return strokeTransition
}