package sliv.tool.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.RadioButton
import javafx.scene.control.SelectionMode
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
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

    private var isDisplayingInfoLabel = false

    private val currentSelectionState: SelectionState
        get() = when {
            areSelectedAllFields -> SelectionState.All
            isSelectionEmpty -> SelectionState.None
            else -> SelectionState.Part
        }
    private val areSelectedAllFields: Boolean
        get() = fileNamesListView.selectedItemsCount == controller.model.frames.count()
    private val isSelectionEmpty: Boolean
        get() = fileNamesListView.selectedItems.isEmpty()
    private val selectedFrames: List<ProjectFrame>
        get() = fileNamesListView.selectedItems.map { it.frame }

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

    private val fileNamesListView = listview(fields) {
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        cellFormat {
            text = it.fileName
            val iconFile =
                this.javaClass.classLoader.getResource("catalogue_image_icon.png")?.openStream()
            if (iconFile != null) {
                graphic = imageview(Image(iconFile)) {
                    fitHeight = 18.0
                    isPreserveRatio = true
                }
            }
        }
    }

    private val catalogueBorderpane = borderpane {
        top = vbox {
            hbox {
                prefWidth = 300.0
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

    private fun getViewFormatNode(withFormat: ViewFormat) = when (withFormat) {
        ViewFormat.FileName -> fileNamesListView
        ViewFormat.ImagePreview -> null // TODO("Add an image preview format")
    }

    private fun updateViewFormatNode(withFormat: ViewFormat) {
        catalogueBorderpane.center = getViewFormatNode(withFormat)
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
        fileNamesListView.selectionModel.selectedItemProperty().onChange {
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
