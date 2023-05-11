package solve.filters.model

import solve.filters.settings.model.FilterSetting
import solve.project.model.ProjectFrame

class Filter(val settings: List<FilterSetting<out Any>>) {
    var enabled: Boolean = true
    val preview: String by lazy { "filter" }

    fun apply(frames: List<ProjectFrame>): List<ProjectFrame> {
        if (!enabled) {
            return frames
        }

        var suitableFrames = frames
        settings.forEach { suitableFrames = it.apply(suitableFrames) }

        return suitableFrames
    }
}
