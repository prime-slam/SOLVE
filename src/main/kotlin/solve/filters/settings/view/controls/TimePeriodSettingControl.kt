package solve.filters.settings.view.controls

import org.controlsfx.control.RangeSlider
import solve.filters.settings.model.TimePeriodFilterSetting
import solve.utils.ceilToInt
import solve.utils.floorToInt
import solve.utils.structures.IntPoint

class TimePeriodSettingControl(
    controlNode: RangeSlider
) : FilterSettingControl<RangeSlider, TimePeriodFilterSetting>(controlNode) {
    override fun extrudeFilterSettings(): TimePeriodFilterSetting {
        val fromTimePeriod = controlNode.lowValue.floorToInt()
        val toTimePeriod = controlNode.highValue.ceilToInt()

        return TimePeriodFilterSetting(IntPoint(fromTimePeriod, toTimePeriod))
    }
}
