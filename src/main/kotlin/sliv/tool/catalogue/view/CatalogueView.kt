package sliv.tool.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Pos
import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.catalogue.model.CatalogueField
import sliv.tool.catalogue.model.ViewFormat
import sliv.tool.project.model.ProjectFrame
import tornadofx.*

object CatalogueResetEvent: FXEvent()

class CatalogueView : View() {
    companion object {
        val initialViewFormat = ViewFormat.FileName
        val initialSelectionState = CatalogueSettingsView.SelectionState.All

        private const val CatalogueWidth = 300.0
    }

    val currentSelectionState: CatalogueSettingsView.SelectionState
        get() {
            val selectionNode = displayingSelectionNode
            selectionNode ?: return initialSelectionState

            return when {
                selectionNode.areSelectedAllItems -> CatalogueSettingsView.SelectionState.All
                selectionNode.isSelectionEmpty -> CatalogueSettingsView.SelectionState.None
                else -> CatalogueSettingsView.SelectionState.Part
            }
        }

    private val settingsView: CatalogueSettingsView by inject()
    private val controller: CatalogueController by inject()

    private val fields = FXCollections.observableArrayList<CatalogueField>()

    private var displayingSelectionNode: SelectionNode? = getSelectableView(initialViewFormat)
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
                        controller.visualizeFramesSelection(fileNamesListView.selectedItems.map { it.frame })
                    }
                }
            }
        }
    }

    override val root = catalogueBorderpane.also { initializeNodes() }

    init {
        controller.model.frames.onChange {
            reinitializeFields()
            visualizeProjectImportSelection()
            resetNodes()
        }
        settingsView.viewFormatProperty.onChange { value ->
            value ?: return@onChange
            displayingSelectionNode = getSelectableView(value)
            updateViewFormatNode(value)
        }
    }

    private fun reinitializeFields() {
        val newFields = controller.model.frames.map { CatalogueField(it) }.toObservable()
        fields.clear()
        fields.addAll(newFields)
    }

    private fun checkForEmptyFields() {
        if (fields.isEmpty()) {
            settingsView.displayInfoLabel("No files found!")
        } else {
            settingsView.hideInfoLabel()
        }
    }

    private fun getSelectableView(withFormat: ViewFormat) = when (withFormat) {
        ViewFormat.FileName -> fileNamesListView
        ViewFormat.ImagePreview -> null // TODO("Add an image preview format")
    }

    private fun updateViewFormatNode(withFormat: ViewFormat) {
        catalogueBorderpane.center = getSelectableView(withFormat)?.root
    }

    private fun initializeNodes() {
        resetNodes()
    }

    private fun visualizeProjectImportSelection() {
        fileNamesListView.selectAllItems()
        controller.visualizeFramesSelection(selectedFrames)
    }

    private fun resetNodes() {
        fire(CatalogueResetEvent)
        checkForEmptyFields()
    }
}
