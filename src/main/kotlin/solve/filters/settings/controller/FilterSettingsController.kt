package solve.filters.settings.controller

import solve.filters.controller.FilterPanelController
import solve.filters.model.Filter
import solve.filters.settings.model.FilterSetting
import tornadofx.*

class FilterSettingsController : Controller() {
    val panelController: FilterPanelController by inject()

    fun createFilter(filterSettings: List<FilterSetting<out Any>>) {
        val filter = Filter(filterSettings)
        panelController.addFilter(filter)
    }

    fun editFilter(editingFilter: Filter, newFilterSettings: List<FilterSetting<out Any>>) {
        val editedFilter = Filter(newFilterSettings)
        editedFilter.enabled = editingFilter.enabled

        panelController.editFilter(editingFilter, editedFilter)
    }
}
