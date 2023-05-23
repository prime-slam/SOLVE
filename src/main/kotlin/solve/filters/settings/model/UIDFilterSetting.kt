package solve.filters.settings.model

import solve.project.model.ProjectFrame

class UIDFilterSetting(uid: Long) : FilterSetting<Long>(uid) {
    override fun apply(fields: List<ProjectFrame>) = fields.filter { it.uids.contains(settingValue) }

    override fun edit(newValue: Long) {
        settingValue = newValue
    }
}
