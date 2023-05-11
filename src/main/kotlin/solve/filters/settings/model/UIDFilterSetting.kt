package solve.filters.settings.model

import solve.project.model.ProjectFrame

class UIDFilterSetting(uid: Long) : FilterSetting<Long> {
    var uid: Long = uid
        private set

    override fun apply(fields: List<ProjectFrame>) = fields.filter { it.uids.contains(uid) }

    override fun edit(newValue: Long) {
        uid = newValue
    }
}
