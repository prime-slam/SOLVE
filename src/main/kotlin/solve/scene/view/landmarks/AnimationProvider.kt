package solve.scene.view.landmarks

import javafx.animation.Timeline
import javafx.animation.Transition
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Shape
import javafx.util.Duration

/**
 * Provides animations.
 */
interface AnimationProvider {
    fun createScaleTransition(node: Node, scaleFactorX: Double, scaleFactorY: Double, duration: Duration): Transition

    fun createFillTransition(shape: Shape, color: Color, duration: Duration): Transition

    fun createStrokeTransition(shape: Shape, color: Color, duration: Duration): Transition

    fun createWidthTransition(shape: Line, targetWidth: Double, duration: Duration): Timeline

    fun createColorTimeline(
        duration: Duration,
        initialColor: Color,
        targetColor: Color,
        colorChangedCallback: (Color) -> Unit
    ): Timeline

    /**
     * Creates a timeline animation, which transforms percent value from 0 to 100
     * and calls given callback with intermediate percent values.
     */
    fun createProgressTimeline(duration: Duration, callback: (Int) -> Unit): Timeline
}
