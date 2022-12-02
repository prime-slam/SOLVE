package sliv.tool.scene.view.utils

import javafx.animation.*
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.util.Duration

fun createScaleAnimation(node: Node, newScale: Double, duration: Duration): Transition {
    val scaleTransition = ScaleTransition()
    scaleTransition.duration = duration
    scaleTransition.toX = newScale
    scaleTransition.toY = newScale
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