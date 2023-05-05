package solve.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.CatalogueField
import solve.catalogue.model.ViewFormat
import solve.catalogue.synchronizeListViewsSelections
import solve.catalogue.view.fields.CatalogueFieldsView
import solve.catalogue.view.fields.CatalogueFileNamesFieldsView
import solve.catalogue.view.fields.CataloguePreviewImagesFieldsView
import solve.constants.IconsCatalogueApplyPath
import solve.filters.view.FilterPanelView
import solve.project.model.ProjectFrame
import solve.utils.addSafely
import solve.utils.createInsetsWithValue
import solve.utils.loadResourcesImage
import solve.utils.removeSafely
import tornadofx.*

class CatalogueView : View() {
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
    private val filterPanelView: FilterPanelView by inject()
    private val controller: CatalogueController by inject()

    private val fields = FXCollections.observableArrayList<CatalogueField>()

    private var displayingFieldsView: CatalogueFieldsView? = null
    private var nonDisplayingFieldsView: CatalogueFieldsView? = null

    private val selectedFrames: List<ProjectFrame>
        get() = displayingFieldsView?.selectedFrames ?: emptyList()

    private val fieldsViewParams = Pair("fields", fields)
    private val fileNamesFieldsView = find<CatalogueFileNamesFieldsView>(fieldsViewParams)
    private val previewImagesFieldsView = find<CataloguePreviewImagesFieldsView>(fieldsViewParams)

    private lateinit var fieldsVBox: VBox

    private val catalogueNode = hbox {
        vbox(5) {
            add(settingsView.root)
            hbox(5) {
                fieldsVBox = vbox {
                    hgrow = Priority.ALWAYS
                }
                button {
                    setPrefSize(ApplyButtonSize, ApplyButtonSize)
                    val buttonImage = loadResourcesImage(IconsCatalogueApplyPath)
                    if (buttonImage != null) {
                        graphic = imageview(buttonImage) {
                            fitHeight = ApplyButtonSize
                            isPreserveRatio = true
                        }
                    }
                    action {
                        applySelection()
                    }
                }
                alignment = Pos.CENTER
                vgrow = Priority.ALWAYS
            }
            border = Border(
                BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
            )
            padding = Insets(5.0, 5.0, 5.0, 10.0)
            hgrow = Priority.ALWAYS
        }
        padding = createInsetsWithValue(5.0)
        vgrow = Priority.ALWAYS
    }

    override val root = vbox {
        add(catalogueNode)
        initializeNodes()
        add(filterPanelView)

        vgrow = Priority.ALWAYS
    }

    init {
        accelerators[KeyCodeCombination(KeyCode.ENTER)] = {
            applySelection()
        }
    }

    fun reinitializeView() {
        reinitializeFields()
        visualizeProjectImportSelection()
        resetNodes()
    }

    fun changeViewFormat(withFormat: ViewFormat) {
        displayingFieldsView = getSelectableView(withFormat)
        val nonDisplayingFormat = when (withFormat) {
            ViewFormat.FileName -> ViewFormat.ImagePreview
            ViewFormat.ImagePreview -> ViewFormat.FileName
        }
        nonDisplayingFieldsView = getSelectableView(nonDisplayingFormat)

        fieldsVBox.removeSafely(nonDisplayingFieldsView?.fieldsListView)
        fieldsVBox.addSafely(displayingFieldsView?.fieldsListView)
    }

    fun selectAllFields() {
        displayingFieldsView?.selectAllItems()
        nonDisplayingFieldsView?.selectAllItems()
    }
    fun deselectAllFields() {
        displayingFieldsView?.deselectAllItems()
        nonDisplayingFieldsView?.deselectAllItems()
    }

    private fun applySelection() {
        controller.visualizeFramesSelection(
            displayingFieldsView?.selectedItems?.map { it.frame } ?: emptyList()
        )
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

    companion object {
        private const val ApplyButtonSize = 15.0
    }
}
