package solve.filters.settings.model

import solve.project.model.ProjectFrame
import solve.utils.structures.IntPoint

class TimePeriodFilterSetting(private var timePeriod: IntPoint) : FilterSetting<IntPoint> {
    override fun apply(fields: List<ProjectFrame>): List<ProjectFrame> =
        fields.filter { it.timestamp in timePeriod.x..timePeriod.y }

    override fun edit(newValue: IntPoint) {
        timePeriod = newValue
    }
}
