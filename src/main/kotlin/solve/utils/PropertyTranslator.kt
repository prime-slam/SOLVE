package solve.utils

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.value.WritableValue
import javafx.util.Duration

class PropertyTranslator<T>(private val property: WritableValue<T>) {
    var activeTimeline: Timeline? = null

    fun translateTo(value: T, durationMillis: Long) {
        activeTimeline?.stop()
        val timeline = Timeline()
        timeline.keyFrames.add(KeyFrame(Duration.millis(durationMillis.toDouble()), KeyValue(property, value)))
        timeline.play()
        timeline.setOnFinished { onActiveTimelineFinished() }
        activeTimeline = timeline
    }

    private fun onActiveTimelineFinished() {
        activeTimeline = null
    }
}
