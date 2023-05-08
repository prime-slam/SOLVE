package solve.scene.view.landmarks

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
import javafx.scene.shape.Line
import javafx.scene.shape.Shape
import javafx.util.Duration
import tornadofx.*

/**
 * Provides real JavaFX animations, used in application.
 */
class JavaFXAnimationProvider : AnimationProvider {
    override fun createScaleTransition(
        node: Node,
        scaleFactorX: Double,
        scaleFactorY: Double,
        duration: Duration
    ): Transition {
        val scaleTransition = ScaleTransition()
        scaleTransition.duration = duration
        scaleTransition.toX = scaleFactorX
        scaleTransition.toY = scaleFactorY
        scaleTransition.node = node
        return scaleTransition
    }

    override fun createFillTransition(shape: Shape, color: Color, duration: Duration): Transition {
        val fillTransition = FillTransition()
        fillTransition.duration = duration
        fillTransition.toValue = color
        fillTransition.shape = shape
        return fillTransition
    }

    override fun createStrokeTransition(shape: Shape, color: Color, duration: Duration): Transition {
        val strokeTransition = StrokeTransition()
        strokeTransition.duration = duration
        strokeTransition.toValue = color
        strokeTransition.shape = shape
        return strokeTransition
    }

    override fun createWidthTransition(shape: Line, targetWidth: Double, duration: Duration): Timeline {
        return Timeline(KeyFrame(duration, KeyValue(shape.strokeWidthProperty(), targetWidth)))
    }

    override fun createColorTimeline(
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

    override fun createProgressTimeline(duration: Duration, callback: (Int) -> Unit): Timeline {
        val percentProperty = SimpleIntegerProperty(0)
        percentProperty.onChange {
            callback(it)
        }

        return Timeline(KeyFrame(duration, KeyValue(percentProperty, 100)))
    }
}
