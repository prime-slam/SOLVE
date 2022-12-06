package solve.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Pos
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.CatalogueField
import solve.catalogue.model.ViewFormat
import solve.catalogue.view.fields.CatalogueFileNamesFieldsView
import solve.catalogue.view.fields.SelectionNode
import solve.project.model.ProjectFrame
import tornadofx.*

class CatalogueView : View() {
    companion object {
        private const val CatalogueWidth = 300.0
    }

    val currentSelectionState: CatalogueSettingsView.SelectionState
        get() {
            val node = displayingSelectionNode ?: nonDisplayingSelectionNode
            node ?: return CatalogueController.initialSelectionState

            return when {
                node.areSelectedAllItems -> CatalogueSettingsView.SelectionState.All
                node.isSelectionEmpty -> CatalogueSettingsView.SelectionState.None
                else -> CatalogueSettingsView.SelectionState.Part
            }
        }

    private val settingsView: CatalogueSettingsView by inject()
    private val controller: CatalogueController by inject()

    private val fields = FXCollections.observableArrayList<CatalogueField>()

    private var displayingSelectionNode: SelectionNode? = null
    private var nonDisplayingSelectionNode: SelectionNode? = null

    private val selectedFrames: List<ProjectFrame>
        get() = displayingSelectionNode?.selectedFrames ?: emptyList()

    private val fileNamesListView = find<CatalogueFileNamesFieldsView>(Pair("fields", fields))

    private val catalogueBorderpane = borderpane {
        prefWidth = CatalogueWidth
        top = settingsView.root
        bottom {
            hbox(alignment = Pos.CENTER) {
                button("Apply") {
                    action {
                        controller.visualizeFramesSelection(
                            displayingSelectionNode?.selectedItems?.map { it.frame } ?: emptyList()
                        )
                    }
                }
            }
        }
    }

    override val root = catalogueBorderpane.also { initializeNodes() }

    fun reinitializeView() {
        reinitializeFields()
        visualizeProjectImportSelection()
        resetNodes()
    }

    fun changeViewFormat(withFormat: ViewFormat) {
        nonDisplayingSelectionNode = displayingSelectionNode
        displayingSelectionNode = getSelectableView(withFormat)
        catalogueBorderpane.center = getSelectableView(withFormat)?.root
    }

    fun selectAllFields() {
        displayingSelectionNode?.selectAllItems()
        nonDisplayingSelectionNode?.selectAllItems()
    }
    fun deselectAllFields() {
        displayingSelectionNode?.deselectAllItems()
        nonDisplayingSelectionNode?.deselectAllItems()
    }

    private fun reinitializeFields() {
        val newFields = controller.model.frames.map { CatalogueField(it) }.toObservable()
        fields.clear()
        fields.addAll(newFields)
    }

    private fun checkForEmptyFields() {
        if (fields.isEmpty()) {
            controller.displayInfoLabel("No files found!")
        } else {
            controller.hideInfoLabel()
        }
    }

    private fun getSelectableView(withFormat: ViewFormat) = when (withFormat) {
        ViewFormat.FileName -> fileNamesListView
        ViewFormat.ImagePreview -> null // TODO("Add an image preview format")
    }

    private fun initializeNodes() {
        changeViewFormat(CatalogueController.initialViewFormat)
        resetNodes()
    }

    private fun visualizeProjectImportSelection() {
        displayingSelectionNode?.selectAllItems()
        controller.visualizeFramesSelection(selectedFrames)
    }

    private fun resetNodes() {
        checkForEmptyFields()
    }
}
