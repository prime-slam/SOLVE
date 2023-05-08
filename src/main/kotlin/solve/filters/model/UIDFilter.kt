package solve.filters.model

import solve.project.model.ProjectFrame

class UIDFilter<Long>(private var uid: Long) : Filter<Long> {
    override fun apply(fields: List<ProjectFrame>) = fields.filter { (it.uids as List<*>).contains(uid) }

    override fun edit(newValue: Long) {
        uid = newValue
    }
}
