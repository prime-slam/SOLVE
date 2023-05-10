package solve.filters.settings.model

import solve.project.model.ProjectFrame

<<<<<<< HEAD
abstract class FilterSetting<T>(settingValue: T) {
    var settingValue: T = settingValue
        protected set

    abstract fun apply(fields: List<ProjectFrame>): List<ProjectFrame>

    abstract fun edit(newValue: T)
=======
interface FilterSetting<T> {
    fun apply(fields: List<ProjectFrame>): List<ProjectFrame>

    fun edit(newValue: T)
>>>>>>> f6786bc (Update filter settings dialog design and add a logic)
}
