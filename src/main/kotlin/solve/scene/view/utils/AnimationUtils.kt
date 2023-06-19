package solve.scene.view.utils

import javafx.animation.FillTransition
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.ScaleTransition
import javafx.animation.StrokeTransition
import javafx.animation.Timeline
import javafx.animation.Transition
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
    duration: Duration,
    initialColor: Color,
    targetColor: Color,
    colorChangedCallback: (Color) -> Unit
) = createProgressTimeline(duration) { percent ->
    val red = getIntermediateValue(initialColor.red, targetColor.red, percent)
    val green = getIntermediateValue(initialColor.green, targetColor.green, percent)
    val blue = getIntermediateValue(initialColor.blue, targetColor.blue, percent)
    val opacity = getIntermediateValue(initialColor.opacity, targetColor.opacity, percent)

    val color = Color(red, green, blue, opacity)
    colorChangedCallback(color)
}

private fun getIntermediateValue(initialValue: Double, targetValue: Double, percent: Int): Double =
    initialValue + (targetValue - initialValue) * (percent / 100.0)

// Creates a timeline animation, which transforms percent value from 0 to 100
// and calls given callback with percent values
fun createProgressTimeline(duration: Duration, callback: (Int) -> Unit): Timeline {
    val percentProperty = SimpleIntegerProperty(0)
    percentProperty.onChange {
        callback(it)
    }

    return Timeline(KeyFrame(duration, KeyValue(percentProperty, 100)))
}
