package solve.scene.view.utils

import javafx.animation.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.util.Duration
import tornadofx.onChange

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

fun createColorTimeline(
    duration: Duration, initialColor: Color, targetColor: Color, colorChangedCallback: (Color) -> Unit
): Timeline {
    return createProgressTimeline(duration) { percent ->
        val red = initialColor.red + (targetColor.red - initialColor.red) * (percent / 100.0)
        val green = initialColor.green + (targetColor.green - initialColor.green) * (percent / 100.0)
        val blue = initialColor.blue + (targetColor.blue - initialColor.blue) * (percent / 100.0)
        val opacity = initialColor.opacity + (targetColor.opacity - initialColor.opacity) * (percent / 100.0)

        val color = Color(red, green, blue, opacity)
        colorChangedCallback(color)
    }
}

fun createProgressTimeline(duration: Duration, callback: (Int) -> Unit): Timeline {
    val percentProperty = SimpleIntegerProperty(0)
    percentProperty.onChange {
        callback(it)
    }

    return Timeline(KeyFrame(duration, KeyValue(percentProperty, 100)))
}