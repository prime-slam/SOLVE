package solve.filters.model

import solve.filters.settings.model.FilterSetting
import solve.filters.settings.model.IndicesStepFilterSetting
import solve.filters.settings.model.TimePeriodFilterSetting
import solve.filters.settings.model.UIDFilterSetting
import solve.project.model.ProjectFrame

class Filter(val settings: List<FilterSetting<out Any>>) {
    var enabled: Boolean = true
    val preview: String by lazy { createPreviewText() }

    fun apply(frames: List<ProjectFrame>): List<ProjectFrame> {
        if (!enabled) {
            return frames
        }

        var suitableFrames = frames
        settings.forEach { suitableFrames = it.apply(suitableFrames) }

        return suitableFrames
    }

    private fun createPreviewText(): String {
        val indicesStepSetting = settings.firstOrNull { it is IndicesStepFilterSetting } as? IndicesStepFilterSetting
        val timePeriodSetting = settings.firstOrNull { it is TimePeriodFilterSetting } as? TimePeriodFilterSetting
        val uidSetting = settings.firstOrNull { it is UIDFilterSetting } as? UIDFilterSetting

        val prefix = "Show "
        val indicesStepPart = if (indicesStepSetting != null) {
            "every ${indicesStepSetting.step} image "
        } else {
            "images "
        }
        val timePeriodPart = if (timePeriodSetting != null) {
            "from ${timePeriodSetting.timePeriod.x} to ${timePeriodSetting.timePeriod.y} "
        } else {
            ""
        }
        val uidPart = if (uidSetting != null) {
            "with landmark ${uidSetting.uid}"
        } else {
            ""
        }

        return "$prefix$indicesStepPart$timePeriodPart$uidPart"
    }
}
