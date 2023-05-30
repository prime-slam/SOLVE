package solve.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.Border
import javafx.scene.layout.BorderPane
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
import solve.constants.IconsCataloguePlaceholder
import solve.filters.view.FilterPanelView
import solve.project.model.ProjectFrame
import solve.styles.CatalogueViewStylesheet
import solve.styles.Style
import solve.utils.addSafely
import solve.utils.loadResourcesImage
import solve.utils.mfxButton
import solve.utils.removeSafely
import tornadofx.*

class CatalogueView : View() {
    val currentSelectionState: CatalogueSettingsView.SelectionState
        get() {
            val node = displayingFieldsView ?: nonDisplayingFieldsView
            node ?: return CatalogueController.initialSelectionState

            return when {
                node.areCheckedAllItems -> CatalogueSettingsView.SelectionState.All
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
        get() = displayingFieldsView?.checkedFrames ?: emptyList()

    private val fieldsViewParams = Pair("fields", fields)
    private val fileNamesFieldsView = find<CatalogueFileNamesFieldsView>(fieldsViewParams)
    private val previewImagesFieldsView = find<CataloguePreviewImagesFieldsView>(fieldsViewParams)

    private lateinit var fieldsVBox: VBox

    private val cataloguePlaceholder = loadResourcesImage(IconsCataloguePlaceholder)

    private val catalogueNode = hbox {
        addStylesheet(CatalogueViewStylesheet::class)
        borderpane {
            style = "-fx-background-color: #${Style.SurfaceColor}"
            top {
                add(settingsView.root)
            }
            center {
                hbox(5) {
                    fieldsVBox = vbox {
                        hgrow = Priority.ALWAYS
                    }
                }
            }
            bottom {
                mfxButton("ADD") {
                    BorderPane.setMargin(this, Insets(5.0, 0.0, 0.0, 0.0))
                    BorderPane.setAlignment(this, Pos.CENTER)
                    setPrefSize(150.0, 31.0)
                    style =
                        "-fx-font-family: ${Style.FontCondensed}; -fx-font-weight: ${Style.FontWeightBold}; " +
                        "-fx-background-color: #${Style.PrimaryColor}; -fx-text-fill: #${Style.SurfaceColor};"

                    action {
                        applySelection()
                    }
                }
            }

            border = Border(
                BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
            )
            padding = Insets(5.0, 5.0, 5.0, 10.0)
            hgrow = Priority.ALWAYS
        }
    }

    private val placeholder = label("Project not imported") {
        tooltip(text)
        padding = Insets(400.0, 0.0, 350.0, 80.0)
        graphic = ImageView(cataloguePlaceholder)
        contentDisplay = ContentDisplay.TOP
        style = "-fx-font-family: ${Style.FontCondensed}; " +
            "-fx-font-size: 24px; -fx-text-fill: ${Style.PrimaryColorLight}"
    }

    override val root =
        vbox {
            style = "-fx-background-color: #${Style.SurfaceColor}"
            add(placeholder)
            fields.onChange {
                this.clear()
                if (fields.isEmpty()) {
                    this.add(placeholder)
                } else {
                    this.add(catalogueNode)
                    initializeNodes()
                    this.add(filterPanelView)
                }
            }

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

    fun checkAllFields() {
        displayingFieldsView?.checkAllItems()
        nonDisplayingFieldsView?.checkAllItems()
    }

    fun uncheckAllFields() {
        displayingFieldsView?.uncheckAllItems()
        nonDisplayingFieldsView?.uncheckAllItems()
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
        displayingFieldsView?.checkAllItems()
        controller.visualizeFramesSelection(selectedFrames)
    }

    private fun resetNodes() {
        checkForEmptyFields()
    }
}
