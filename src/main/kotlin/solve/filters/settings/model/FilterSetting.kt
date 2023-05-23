package solve.filters.settings.model

import solve.project.model.ProjectFrame

abstract class FilterSetting<T>(settingValue: T) {
    var settingValue: T = settingValue
        protected set

    abstract fun apply(fields: List<ProjectFrame>): List<ProjectFrame>

    abstract fun edit(newValue: T)
}
