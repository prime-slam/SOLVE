package solve.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import solve.catalogue.addSafely
import solve.catalogue.view.fields.CataloguePreviewImagesFieldsView
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.CatalogueField
import solve.catalogue.model.ViewFormat
import solve.catalogue.removeSafely
import solve.catalogue.view.fields.CatalogueFileNamesFieldsView
import solve.project.model.ProjectFrame
import solve.catalogue.synchronizeListViewsSelections
import solve.catalogue.view.fields.CatalogueFieldsView
import solve.utils.loadImage
import tornadofx.*

class CatalogueView : View() {
    companion object {
        private const val ApplyButtonSize = 15.0
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

    private val selectedFrames: List<ProjectFrame>
        get() = displayingFieldsView?.selectedFrames ?: emptyList()

    private val fieldsViewArgs = Pair("fields", fields)
    private val fileNamesFieldsView = find<CatalogueFileNamesFieldsView>(fieldsViewArgs)
    private val previewImagesFieldsView = find<CataloguePreviewImagesFieldsView>(fieldsViewArgs)

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
                    val buttonImage = loadImage("icons/catalogue_apply_icon.png")
                    if (buttonImage != null) {
                        graphic = imageview(buttonImage) {
                            fitHeight = ApplyButtonSize
                            isPreserveRatio = true
                        }
                    }
                    action {
                        controller.visualizeFramesSelection(
                            displayingFieldsView?.selectedItems?.map { it.frame } ?: emptyList()
                        )
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
        padding = Insets(5.0, 5.0, 5.0, 5.0)
        vgrow = Priority.ALWAYS
    }

    override val root = catalogueNode.also { initializeNodes() }

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
