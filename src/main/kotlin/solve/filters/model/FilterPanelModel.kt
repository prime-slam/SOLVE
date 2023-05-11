package solve.filters.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList

class FilterPanelModel {
    private val _filters = FXCollections.observableArrayList<Filter>()
    val filters: ObservableList<Filter> = FXCollections.unmodifiableObservableList(_filters)

    fun addFilter(filter: Filter) {
        _filters.add(filter)
    }

    fun removeFilter(filter: Filter) {
        _filters.remove(filter)
    }

    fun replaceFilter(oldFilter: Filter, newFilter: Filter) {
        if (!_filters.contains(oldFilter)) {
            return
        }

        val oldFilterIndex = _filters.indexOf(oldFilter)
        _filters.remove(oldFilter)
        _filters.add(oldFilterIndex, newFilter)
    }
}