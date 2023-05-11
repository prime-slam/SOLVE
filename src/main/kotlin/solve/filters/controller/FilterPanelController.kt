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

    init {
        addBindings()
    }

    fun addFilter(filter: Filter) {
        model.addFilter(filter)
    }

    fun removeFilter(filter: Filter) {
        model.removeFilter(filter)
    }

    fun editFilter(editingFilter: Filter, editedFilter: Filter) {
        model.replaceFilter(editingFilter, editedFilter)
    }

    fun applyFilters() {
        val filteredFrames = getFilteredProjectFrames()
        catalogueController.setCatalogueFrames(filteredFrames)
    }

    private fun addBindings() {
        projectController.model.projectProperty.onChange {
            applyFilters()
        }
        model.filters.onChange {
            applyFilters()
        }
    }

    private fun getFilteredProjectFrames(): List<ProjectFrame> {
        val projectFrames = projectController.model.project.frames

        if (model.filters.isEmpty()) {
            return projectFrames
        }

        val enabledFilters = model.filters.filter { it.enabled }
        val notOrderedFilteredFrames = enabledFilters.map { it.apply(projectFrames) }.flatten().distinct()
        val projectFrameToIndexMap = projectFrames.indices.associateBy { index -> projectFrames[index] }

        return notOrderedFilteredFrames.sortedBy { frame -> projectFrameToIndexMap[frame] }
    }
}
