package solve.filters.settings.view.controls

import org.controlsfx.control.RangeSlider
import solve.filters.settings.model.TimePeriodFilterSetting
import solve.utils.ceilToInt
import solve.utils.floorToInt
import solve.utils.structures.IntPoint

object TimePeriodSettingNodeConnector : FilterSettingNodeConnector<RangeSlider, TimePeriodFilterSetting>(
    RangeSlider::class,
    TimePeriodFilterSetting::class
) {
    override fun extractFilterSettingsFromTypedSettingNode(settingNode: RangeSlider): TimePeriodFilterSetting? {
        val fromTimePeriod = settingNode.lowValue.floorToInt()
        val toTimePeriod = settingNode.highValue.ceilToInt()

        return TimePeriodFilterSetting(IntPoint(fromTimePeriod, toTimePeriod))
    }

    override fun updateTypedSettingNodeWithSettings(settingNode: RangeSlider, setting: TimePeriodFilterSetting) {
        settingNode.lowValue = setting.timePeriod.x.toDouble()
        settingNode.highValue = setting.timePeriod.y.toDouble()
    }

    override fun setDefaultTypedSettingNodeState(settingNode: RangeSlider) {
        settingNode.min = 0.0
        settingNode.max = 1.0
        settingNode.lowValue = settingNode.min
        settingNode.highValue = settingNode.max
    }
}
