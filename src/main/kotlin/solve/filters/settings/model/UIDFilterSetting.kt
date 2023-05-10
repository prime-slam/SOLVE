package solve.filters.settings.model

import solve.project.model.ProjectFrame

<<<<<<< HEAD
class UIDFilterSetting(uid: Long) : FilterSetting<Long>(uid) {
    override fun apply(fields: List<ProjectFrame>) = fields.filter { it.uids.contains(settingValue) }

    override fun edit(newValue: Long) {
        settingValue = newValue
=======
class UIDFilterSetting(private var uid: Long) : FilterSetting<Long> {
    override fun apply(fields: List<ProjectFrame>) = fields.filter { it.uids.contains(uid) }

    override fun edit(newValue: Long) {
        uid = newValue
>>>>>>> f6786bc (Update filter settings dialog design and add a logic)
    }
}
