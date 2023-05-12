package solve.filters.controller

import solve.catalogue.controller.CatalogueController
import solve.filters.model.Filter
import solve.filters.model.FilterPanelModel
import solve.project.controller.ProjectController
import solve.project.model.ProjectFrame
import tornadofx.*

class FilterPanelController : Controller() {
    val model = FilterPanelModel()

    private val projectController: ProjectController by inject()
    private val catalogueController: CatalogueController by inject()

    fun addFilter(filter: Filter) {
        model.addFilter(filter)
    }

    fun removeFilter(filter: Filter) {
        model.removeFilter(filter)
    }

    fun editFilter(editingFilter: Filter, newFilter: Filter) {
        model.replaceFilter(editingFilter, newFilter)
    }

    fun applyFilters() {
        val filteredFrames = getFilteredProjectFrames()
        catalogueController.setCatalogueFrames(filteredFrames)
    }

    private fun getFilteredProjectFrames(): List<ProjectFrame> {
        val projectFrames = projectController.model.project.frames

        val enabledFilters = model.filters.filter { it.enabled }

        val notOrderedFilteredFrames = enabledFilters.map { it.apply(projectFrames) }.flatten().distinct()
        val projectFrameToIndexMap = projectFrames.indices.associateBy { index -> projectFrames[index] }

        return notOrderedFilteredFrames.sortedBy { frame -> projectFrameToIndexMap[frame] }
    }
}
