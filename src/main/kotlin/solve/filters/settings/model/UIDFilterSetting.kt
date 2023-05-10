package solve.filters.settings.model

import solve.project.model.ProjectFrame

class UIDFilterSetting(private var uid: Long) : FilterSetting<Long> {
    override fun apply(fields: List<ProjectFrame>) = fields.filter { it.uids.contains(uid) }

    override fun edit(newValue: Long) {
        uid = newValue
    }
}
