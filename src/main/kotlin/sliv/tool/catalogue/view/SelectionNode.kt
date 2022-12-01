package sliv.tool.catalogue.view

import sliv.tool.catalogue.model.CatalogueField
import sliv.tool.project.model.ProjectFrame

interface SelectionNode {
    val areSelectedAllItems: Boolean

    val isSelectionEmpty: Boolean

    val selectedItems: List<CatalogueField>

    val selectedFrames: List<ProjectFrame>

    fun selectAllItems()

    fun deselectAllItems()
}
