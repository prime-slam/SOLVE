package solve.filters.controller

import solve.filters.model.Filter
import solve.filters.model.FilterPanelModel
import tornadofx.*

class FilterPanelController : Controller() {
    val model = FilterPanelModel()

    fun addFilter(filter: Filter) {
        model.addFilter(filter)
    }

    fun removeFilter(filter: Filter) {
        model.removeFilter(filter)
    }

    fun editFilter(oldFilter: Filter, editedFilter: Filter) {
        model.replaceFilter(oldFilter, editedFilter)
    }
}
