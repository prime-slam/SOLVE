package solve.filters.model

import solve.project.model.ProjectFrame

interface Filter {
    fun apply(frames: List<ProjectFrame>): List<ProjectFrame>
}
