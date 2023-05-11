package solve.filters.settings.model

import solve.project.model.ProjectFrame

class IndicesStepFilterSetting(step: Int) : FilterSetting<Int> {
    var step: Int = step
        private set

    override fun apply(fields: List<ProjectFrame>) = fields.slice(0..fields.lastIndex step this.step)

    override fun edit(newValue: Int) {
        step = newValue
    }
}
