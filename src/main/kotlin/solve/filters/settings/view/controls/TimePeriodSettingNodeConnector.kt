package solve.filters.settings.view.controls

import org.controlsfx.control.RangeSlider
import solve.filters.settings.model.TimePeriodFilterSetting
import solve.project.controller.ProjectController
import solve.project.model.ProjectFrame
import solve.utils.ceilToInt
import solve.utils.floorToInt
import solve.utils.structures.IntPoint
import tornadofx.*

object TimePeriodSettingNodeConnector : FilterSettingNodeConnector<RangeSlider, TimePeriodFilterSetting>(
    RangeSlider::class,
    TimePeriodFilterSetting::class
) {
    private val projectController: ProjectController = find()

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
        val projectFrames = projectController.model.project.frames

        settingNode.min = getFramesMinTimestamp(projectFrames).toDouble()
        settingNode.max = getFramesMaxTimestamp(projectFrames).toDouble()
        settingNode.lowValue = settingNode.min
        settingNode.highValue = settingNode.max
    }

    private fun getFramesMinTimestamp(frames: List<ProjectFrame>) = frames.minOf { it.timestamp }

    private fun getFramesMaxTimestamp(frames: List<ProjectFrame>) = frames.maxOf { it.timestamp }
}
