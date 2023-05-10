package solve.filters.model

import solve.filters.settings.model.FilterSetting
import solve.project.model.ProjectFrame

data class Filter(val settings: List<FilterSetting<Any>>) {
    fun apply(frames: List<ProjectFrame>): List<ProjectFrame> {
        var suitableFrames = frames
        settings.forEach { suitableFrames = it.apply(suitableFrames) }

        return suitableFrames
    }
}
