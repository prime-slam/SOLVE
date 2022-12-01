package sliv.tool.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import sliv.tool.catalogue.*
import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.catalogue.model.CatalogueField
import sliv.tool.catalogue.model.ViewFormat
import sliv.tool.project.model.ProjectFrame
import tornadofx.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class CatalogueView : View() {
    enum class SelectionState {
        All,
        None,
        Part
    }

    companion object {
        private val initialViewFormat = ViewFormat.FileName
        private val initialSelectionState = SelectionState.All

        private const val CatalogueWidth = 300.0
    }

    private val controller: CatalogueController by inject()

    private val fields = FXCollections.observableArrayList<CatalogueField>()

    private val viewFormatToggleGroup = ToggleGroup()
    private lateinit var fileNameViewRadioButton: RadioButton
    private lateinit var imagePreviewRadioButton: RadioButton
    private var viewFormat: ViewFormat by viewFormatRadioButtonDelegate()

    private val selectionCheckBoxBoolProperty = booleanProperty(false)
    private lateinit var selectionCheckBox: CheckBox
    private var checkBoxSelectionState: SelectionState by checkBoxSelectionStateDelegate()
    private val displayingSelectionNode: SelectionNode?
        get() = getSelectableView(viewFormat)
    private val selectedFrames: List<ProjectFrame>
        get() = displayingSelectionNode?.selectedFrames ?: emptyList()

    private var isDisplayingInfoLabel = false

    private val currentSelectionState: SelectionState
        get() {
            val selectionNode = displayingSelectionNode
            selectionNode ?: return initialSelectionState

            return when {
                selectionNode.areSelectedAllItems -> SelectionState.All
                selectionNode.isSelectionEmpty -> SelectionState.None
                else -> SelectionState.Part
            }
        }

    init {
        controller.model.frames.onChange {
            reinitializeFields()
            visualizeProjectImportSelection()
            resetNodes()
        }
    }

    private fun reinitializeFields() {
        val newFields = controller.model.frames.map { CatalogueField(it) }.toObservable()
        fields.clear()
        fields.addAll(newFields)
    }

    private val infoLabel = label()

    private val fileNamesListView = CatalogueFileNamesFieldsView(fields)

    private val catalogueBorderpane = borderpane {
        prefWidth = CatalogueWidth
        top = vbox {
            hbox {
                padding = Insets(5.0, 5.0, 5.0, 5.0)
                spacing = 5.0
                selectionCheckBox = checkbox("Select all", selectionCheckBoxBoolProperty) {
                    action {
                        if (isSelected) {
                            fileNamesListView.selectAllItems()
                        } else {
                            fileNamesListView.deselectAllItems()
                        }
                    }
                }
                pane().hgrow = Priority.ALWAYS
                fileNameViewRadioButton = radiobutton("File view", viewFormatToggleGroup)
                imagePreviewRadioButton = radiobutton("Image preview", viewFormatToggleGroup)
            }
            add(infoLabel)
        }
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

    private fun displayInfoLabel(withText: String) {
        infoLabel.text = withText
        infoLabel.isVisible = true
        infoLabel.isManaged = true
        isDisplayingInfoLabel = true
    }

    private fun hideInfoLabel() {
        if (!isDisplayingInfoLabel) {
            return
        }

        infoLabel.isManaged = false
        infoLabel.isVisible = false
        isDisplayingInfoLabel = false
    }

    private fun checkForEmptyFields() {
        if (fields.isEmpty()) {
            displayInfoLabel("No files found!")
        } else {
            hideInfoLabel()
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
        initializeViewFormatRadioButtons()
        initializeSelectionNodes()
        resetNodes()
    }

    private fun getRadioButtonViewFormat(radioButton: RadioButton) = when (radioButton) {
        fileNameViewRadioButton -> ViewFormat.FileName
        imagePreviewRadioButton -> ViewFormat.ImagePreview
        else -> ViewFormat.FileName.also { println("Unexpected view format radio button!") }
    }

    private fun viewFormatRadioButtonDelegate(): ReadWriteProperty<Any?, ViewFormat> =
        object : ReadWriteProperty<Any?, ViewFormat> {
            var currentValue = initialViewFormat
            override fun getValue(thisRef: Any?, property: KProperty<*>): ViewFormat = currentValue
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: ViewFormat) {
                currentValue = value
                updateViewFormatNode(currentValue)
                viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(currentValue))
            }
        }

    private fun getViewFormatRadioButton(viewFormat: ViewFormat) = when (viewFormat) {
        ViewFormat.FileName -> fileNameViewRadioButton
        ViewFormat.ImagePreview -> imagePreviewRadioButton
    }

    private fun initializeViewFormatRadioButtons() {
        viewFormatToggleGroup.selectedToggleProperty().onChange {
            it ?: return@onChange
            viewFormat = getRadioButtonViewFormat(it as RadioButton)
        }
    }

    private fun checkBoxSelectionStateDelegate(): ReadWriteProperty<Any?, SelectionState> =
        object : ReadWriteProperty<Any?, SelectionState> {
            var currentValue = initialSelectionState
            override fun getValue(thisRef: Any?, property: KProperty<*>): SelectionState = currentValue
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: SelectionState) {
                currentValue = value
                when (currentValue) {
                    SelectionState.All -> {
                        selectionCheckBox.isIndeterminate = false
                        selectionCheckBoxBoolProperty.value = true
                    }
                    SelectionState.None -> {
                        selectionCheckBox.isIndeterminate = false
                        selectionCheckBoxBoolProperty.value = false
                    }
                    SelectionState.Part -> {
                        selectionCheckBox.isIndeterminate = true
                        selectionCheckBoxBoolProperty.value = false
                    }
                }
            }
        }

    private fun initializeSelectionNodes() {
        fileNamesListView.root.onSelectionChanged {
            checkBoxSelectionState = currentSelectionState
        }
        fileNamesListView.root.setOnMouseClicked {
            checkBoxSelectionState = currentSelectionState
        }
    }

    private fun visualizeProjectImportSelection() {
        fileNamesListView.selectAllItems()
        controller.visualizeFramesSelection(selectedFrames)
    }

    private fun resetNodes() {
        checkBoxSelectionState = currentSelectionState
        viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(initialViewFormat))
        checkForEmptyFields()
    }
}
