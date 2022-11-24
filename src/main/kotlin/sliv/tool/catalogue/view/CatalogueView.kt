package sliv.tool.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.RadioButton
import javafx.scene.control.SelectionMode
import javafx.scene.control.Toggle
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import sliv.tool.catalogue.*
import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.catalogue.model.CatalogueField
import sliv.tool.catalogue.model.ViewFormat
import tornadofx.*

class CatalogueView : View() {
    enum class SelectionState {
        All,
        None,
        Part
    }

    companion object {
        private val initialViewFormat = ViewFormat.FileName
    }

    private val controller: CatalogueController by inject()

    private var fields = FXCollections.observableArrayList<CatalogueField>()

    private val viewFormatToggleGroup = ToggleGroup()
    private lateinit var fileNameViewRadioButton: RadioButton
    private lateinit var imagePreviewRadioButton: RadioButton
    private var currentViewFormat = initialViewFormat

    private val selectionCheckBoxBoolProperty = booleanProperty(false)
    private lateinit var selectionCheckBox: CheckBox

    private var previousSelectedField: CatalogueField? = null
    private var previousSelectedFieldsCount = 0
    private val currentSelectedField: CatalogueField?
        get() = fileNamesListView.selectedItem
    private val currentSelectionState: SelectionState
        get() = when {
            areSelectedAllFields -> SelectionState.All
            areNotSelectedAllFields -> SelectionState.None
            else -> SelectionState.Part
        }
    private val areSelectedAllFields: Boolean
        get() = fileNamesListView.selectedItemsCount() == controller.model.frames.count()
    private val areNotSelectedAllFields: Boolean
        get() = fileNamesListView.selectedItems().isEmpty()

    init {
        controller.model.frames.onChange {
            resetNodes()
            fields.clear()
            fields.addAll(getFields())
        }
    }

    private fun getFields() = controller.model.frames.map { CatalogueField(it) }.toObservable()

    private val fileNamesListView = listview(fields) {
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        cellFormat {
            text = it.fileName
        }
    }

    private val catalogueBorderpane = borderpane {
        top = hbox {
            prefWidth = 300.0
            padding = Insets(5.0, 5.0, 5.0, 5.0)
            spacing = 5.0
            selectionCheckBox = checkbox("Select all", selectionCheckBoxBoolProperty) {
                action {
                    println(isSelected)
                    if (isSelected) fileNamesListView.selectAllItems()
                    else fileNamesListView.deselectAllItems()
                }
            }
            pane().hgrow = Priority.ALWAYS
            fileNameViewRadioButton = radiobutton("File view", viewFormatToggleGroup)
            imagePreviewRadioButton = radiobutton("Image preview", viewFormatToggleGroup)
        }
        bottom {
            hbox(alignment = Pos.CENTER) {
                button("Apply") {
                    action {
                        controller.createFramesSelection(fileNamesListView.selectedItems().map { it.frame })
                    }
                }
            }
        }
    }

    override val root = catalogueBorderpane.also { initializeNodes() }

    private fun onViewFormatToggleSwitched(switchedToToggle: Toggle) {
        val selectedViewFormat = when (switchedToToggle as RadioButton) {
            fileNameViewRadioButton -> ViewFormat.FileName
            imagePreviewRadioButton -> ViewFormat.ImagePreview
            else -> ViewFormat.FileName.also { println("Unexpected view format toggle!") }
        }
        updateViewFormatNode(selectedViewFormat)
    }

    private fun getViewFormatNode(withFormat: ViewFormat) = when (withFormat) {
        ViewFormat.FileName -> fileNamesListView
        ViewFormat.ImagePreview -> null // TODO("Add an image preview format")
    }
    private fun updateViewFormatNode(withFormat: ViewFormat) {
        catalogueBorderpane.center = getViewFormatNode(withFormat)
    }

    private fun initializeNodes() {
        initializeViewFormatToggles()
        initializeSelectionNodes()
    }

    private fun getViewFormatToggle(viewFormat: ViewFormat) = when (viewFormat) {
        ViewFormat.FileName -> fileNameViewRadioButton
        ViewFormat.ImagePreview -> imagePreviewRadioButton
    }

    private fun initializeViewFormatToggles() {
        viewFormatToggleGroup.selectedToggleProperty().onChange {
            it ?: return@onChange
            onViewFormatToggleSwitched(it)
        }
        viewFormatToggleGroup.selectToggle(getViewFormatToggle(currentViewFormat))
    }

    private fun setSelectionCheckBoxState(selectionState: SelectionState) {
        when (selectionState) {
            SelectionState.All -> {
                selectionCheckBox.isIndeterminate = false
                selectionCheckBoxBoolProperty.value = true
            }
            SelectionState.None -> {
                selectionCheckBox.isIndeterminate = false
                selectionCheckBoxBoolProperty.value = false
            }
            SelectionState.Part -> selectionCheckBox.isIndeterminate = true
        }
    }

    private fun haveFieldDoubleClick() = previousSelectedField != null && previousSelectedField == currentSelectedField

    private fun initializeSelectionNodes() {
        fileNamesListView.setOnMouseClicked {
            if (haveFieldDoubleClick() && previousSelectedFieldsCount == 1) {
                fileNamesListView.deselectAllItems()
                fileNamesListView.selectItem(currentSelectedField)
            }
            previousSelectedField = currentSelectedField
            previousSelectedFieldsCount = fileNamesListView.selectedItemsCount()
        }
        fileNamesListView.selectionModel.selectedItemProperty().onChange {
            setSelectionCheckBoxState(currentSelectionState)
        }
    }

    private fun resetNodes() {
        setSelectionCheckBoxState(SelectionState.None)
        viewFormatToggleGroup.selectToggle(getViewFormatToggle(initialViewFormat))
    }
}
