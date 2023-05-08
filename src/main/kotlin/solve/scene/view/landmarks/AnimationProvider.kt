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
    /**
     * Creates transition which transforms node scale to target value of scaleFactorX and scaleFactorY.
     */
    fun createScaleTransition(node: Node, scaleFactorX: Double, scaleFactorY: Double, duration: Duration): Transition

    /**
     * Creates transition which transforms shape's color to the target value across all intermediate values.
     */
    fun createFillTransition(shape: Shape, color: Color, duration: Duration): Transition

    /**
     * Creates transition which transforms shape's color to the target value across all intermediate values.
     */
    fun createStrokeTransition(shape: Shape, color: Color, duration: Duration): Transition

    fun createWidthTransition(shape: Line, targetWidth: Double, duration: Duration): Timeline

    /**
     * Creates transition which transforms initialColor to targetColor across all intermediate values
     * and calls colorChangedCallback with each intermediate value corresponding to percent value
     * from 0 to 100.
     */
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
