package solve.filters.settings.model

import solve.project.model.ProjectFrame
import solve.utils.structures.IntPoint

<<<<<<< HEAD
class TimePeriodFilterSetting(timePeriod: IntPoint) : FilterSetting<IntPoint>(timePeriod) {
    override fun apply(fields: List<ProjectFrame>): List<ProjectFrame> =
        fields.filter { it.timestamp in settingValue.x..settingValue.y }

    override fun edit(newValue: IntPoint) {
        settingValue = newValue
=======
class TimePeriodFilterSetting(private var timePeriod: IntPoint) : FilterSetting<IntPoint> {
    override fun apply(fields: List<ProjectFrame>): List<ProjectFrame> =
        fields.filter { it.timestamp in timePeriod.x..timePeriod.y }

    override fun edit(newValue: IntPoint) {
        timePeriod = newValue
>>>>>>> f6786bc (Update filter settings dialog design and add a logic)
    }
}
