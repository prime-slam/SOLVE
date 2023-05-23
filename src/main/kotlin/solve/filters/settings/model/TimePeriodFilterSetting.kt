package solve.filters.settings.model

import solve.project.model.ProjectFrame
import solve.utils.structures.IntPoint

class TimePeriodFilterSetting(timePeriod: IntPoint) : FilterSetting<IntPoint>(timePeriod) {
    override fun apply(fields: List<ProjectFrame>): List<ProjectFrame> =
        fields.filter { it.timestamp in settingValue.x..settingValue.y }

    override fun edit(newValue: IntPoint) {
        settingValue = newValue
    }
}
