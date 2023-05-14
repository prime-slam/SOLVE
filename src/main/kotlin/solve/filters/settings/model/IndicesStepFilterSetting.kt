package solve.filters.settings.model

import solve.project.model.ProjectFrame

class IndicesStepFilterSetting(step: Int) : FilterSetting<Int>(step) {
    override fun apply(fields: List<ProjectFrame>) = fields.slice((settingValue - 1)..fields.lastIndex step this.settingValue)

    override fun edit(newValue: Int) {
        settingValue = newValue
    }
}
