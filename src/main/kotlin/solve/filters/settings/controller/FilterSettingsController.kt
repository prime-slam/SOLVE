package solve.filters.settings.controller

import solve.filters.controller.FilterPanelController
import solve.filters.model.Filter
import solve.filters.settings.model.FilterSetting
import tornadofx.*

class FilterSettingsController : Controller() {
    val panelController: FilterPanelController by inject()

    fun createFilter(filterSettings: List<FilterSetting<Any>>) {
        val filter = Filter(filterSettings)
        panelController.addFilter(filter)
    }

    fun createEditedFilter(oldFilter: Filter, editedFilterSettings: List<FilterSetting<Any>>) {
        val editedFilter = Filter(editedFilterSettings)
        panelController.editFilter(oldFilter, editedFilter)
    }
}