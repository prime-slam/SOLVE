package solve.filters.model

import solve.project.model.ProjectFrame

class IndicesStepFilter(private var step: Int) : Filter<Int> {
    override fun apply(fields: List<ProjectFrame>) = fields.slice(0..fields.lastIndex step this.step)

    override fun edit(newValue: Int) {
        step = newValue
    }
}
