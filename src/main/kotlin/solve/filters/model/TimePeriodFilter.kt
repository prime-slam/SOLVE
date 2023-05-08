package solve.filters.model

import solve.project.model.ProjectFrame
import solve.utils.structures.IntPoint

class TimePeriodFilter(private var timePeriod: IntPoint) : Filter<IntPoint> {
    override fun apply(fields: List<ProjectFrame>): List<ProjectFrame> =
        fields.filter { it.timestamp in timePeriod.x..timePeriod.y }

    override fun edit(newValue: IntPoint) {
        timePeriod = newValue
    }
}
