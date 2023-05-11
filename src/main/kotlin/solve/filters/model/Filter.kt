package solve.filters.model

import solve.filters.settings.model.FilterSetting
import solve.project.model.ProjectFrame

data class Filter(val settings: List<FilterSetting<out Any>>) {
    val preview: String by lazy { "filter" }
    fun apply(frames: List<ProjectFrame>): List<ProjectFrame> {
        var suitableFrames = frames
        settings.forEach { suitableFrames = it.apply(suitableFrames) }

        return suitableFrames
    }
}
