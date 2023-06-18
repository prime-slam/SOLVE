package solve.filters.settings.model

import solve.project.model.ProjectFrame

abstract class FilterSetting<T>(settingValue: T) {
    var enabled: Boolean = true

    var settingValue: T = settingValue
        protected set

    fun apply(fields: List<ProjectFrame>) : List<ProjectFrame> {
        if (!enabled)
            return fields;

        return applySetting(fields)
    }
    protected abstract fun applySetting(fields: List<ProjectFrame>): List<ProjectFrame>

    abstract fun edit(newValue: T)
}
