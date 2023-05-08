package solve.filters.model

import solve.project.model.ProjectFrame

interface Filter<T> {
    fun apply(fields: List<ProjectFrame>): List<ProjectFrame>

    fun edit(newValue: T)
}
