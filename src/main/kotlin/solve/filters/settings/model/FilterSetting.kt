package solve.filters.settings.model

import solve.project.model.ProjectFrame

interface FilterSetting<T> {
    fun apply(fields: List<ProjectFrame>): List<ProjectFrame>

    fun edit(newValue: T)
}
