package solve.filters.settings.model

import solve.project.model.ProjectFrame

<<<<<<< HEAD
class IndicesStepFilterSetting(step: Int) : FilterSetting<Int>(step) {
    override fun apply(fields: List<ProjectFrame>) = fields.slice(
        (settingValue - 1)..fields.lastIndex step this.settingValue
    )

    override fun edit(newValue: Int) {
        settingValue = newValue
=======
class IndicesStepFilterSetting(private var step: Int) : FilterSetting<Int> {
    override fun apply(fields: List<ProjectFrame>) = fields.slice(0..fields.lastIndex step this.step)

    override fun edit(newValue: Int) {
        step = newValue
>>>>>>> f6786bc (Update filter settings dialog design and add a logic)
    }
}
