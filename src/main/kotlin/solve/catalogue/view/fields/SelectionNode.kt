package solve.catalogue.view.fields

import solve.catalogue.model.CatalogueField
import solve.project.model.ProjectFrame
import tornadofx.FXEvent

interface SelectionNode {
    val areSelectedAllItems: Boolean

    val isSelectionEmpty: Boolean

    val selectedItems: List<CatalogueField>

    val selectedFrames: List<ProjectFrame>

    fun selectAllItems()

    fun deselectAllItems()
}
