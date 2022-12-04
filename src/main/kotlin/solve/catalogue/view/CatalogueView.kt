package solve.catalogue.view

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import solve.catalogue.view.fields.CataloguePreviewImagesFieldsView
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.CatalogueField
import solve.catalogue.model.ViewFormat
import solve.catalogue.view.fields.CatalogueFileNamesFieldsView
import solve.catalogue.view.fields.SelectionNode
import solve.project.model.ProjectFrame
import solve.catalogue.synchronizeListViewsSelections
import solve.catalogue.view.fields.CatalogueFieldsView
import tornadofx.*

class CatalogueView : View() {
    companion object {
        private const val CatalogueWidth = 300.0
    }

    val currentSelectionState: CatalogueSettingsView.SelectionState
        get() {
            val node = displayingFieldsView ?: nonDisplayingFieldsView
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

    private var displayingFieldsView: CatalogueFieldsView? = null
    private var nonDisplayingFieldsView: CatalogueFieldsView? = null
    private var displayingSelectionListener: ListChangeListener<Int>? = null

    private val selectedFrames: List<ProjectFrame>
        get() = displayingFieldsView?.selectedFrames ?: emptyList()

    private val fieldsViewArgs = Pair("fields", fields)
    private val fileNamesFieldsView = find<CatalogueFileNamesFieldsView>(fieldsViewArgs)
    private val previewImagesFieldsView = find<CataloguePreviewImagesFieldsView>(fieldsViewArgs)

    private val catalogueBorderpane = borderpane {
        prefWidth = CatalogueWidth
        top = settingsView.root
        bottom {
            hbox(alignment = Pos.CENTER) {
                button("Apply") {
                    action {
                        controller.visualizeFramesSelection(
                            displayingFieldsView?.selectedItems?.map { it.frame } ?: emptyList()
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
        displayingFieldsView = getSelectableView(withFormat)
        nonDisplayingFieldsView = getSelectableView(withFormat.differentFormat())

        catalogueBorderpane.center = displayingFieldsView?.fieldsListView
    }

    fun selectAllFields() {
        displayingFieldsView?.selectAllItems()
        nonDisplayingFieldsView?.selectAllItems()
    }
    fun deselectAllFields() {
        displayingFieldsView?.deselectAllItems()
        nonDisplayingFieldsView?.deselectAllItems()
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

    private fun getSelectableView(withFormat: ViewFormat): CatalogueFieldsView = when (withFormat) {
        ViewFormat.FileName -> fileNamesFieldsView
        ViewFormat.ImagePreview -> previewImagesFieldsView
    }

    private fun initializeNodes() {
        synchronizeListViewsSelections(fileNamesFieldsView.fieldsListView, previewImagesFieldsView.fieldsListView)
        changeViewFormat(CatalogueController.initialViewFormat)
        resetNodes()
    }

    private fun visualizeProjectImportSelection() {
        displayingFieldsView?.selectAllItems()
        controller.visualizeFramesSelection(selectedFrames)
    }

    private fun resetNodes() {
        checkForEmptyFields()
    }
}
