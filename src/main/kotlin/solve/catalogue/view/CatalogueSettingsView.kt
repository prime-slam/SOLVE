package solve.catalogue.view

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.ViewFormat
import solve.catalogue.addNameTooltip
import tornadofx.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CatalogueSettingsView: View() {
    enum class SelectionState {
        None,
        Part,
        All;
    }

    private val controller: CatalogueController by inject()

    private val isSelectionCheckBoxCheckedProperty = booleanProperty(false)
    private var isSelectionCheckBoxChecked: Boolean by isSelectionCheckBoxCheckedProperty

    private var viewFormatProperty = SimpleObjectProperty(CatalogueController.initialViewFormat)
    private var viewFormat: ViewFormat by viewFormatProperty

    private val catalogueView: CatalogueView by inject()

    private val viewFormatToggleGroup = ToggleGroup()
    private lateinit var fileNameViewRadioButton: RadioButton
    private lateinit var imagePreviewRadioButton: RadioButton

    private lateinit var selectionCheckBox: CheckBox
    private var checkBoxSelectionState: SelectionState by checkBoxSelectionStateDelegate()

    private val infoLabel = label()
    private val infoNode = vbox {
        separator()
        add(infoLabel)
        alignment = Pos.CENTER
    }
    private var isDisplayingInfoLabel = false

    override val root = vbox {
        hbox(5) {
            selectionCheckBox = checkbox("Select all", isSelectionCheckBoxCheckedProperty) {
                addNameTooltip()
                action {
                    if (isSelected) {
                        controller.selectAllFields()
                    } else {
                        controller.deselectAllFields()
                    }
                }
            }
            pane().hgrow = Priority.ALWAYS
            fileNameViewRadioButton = radiobutton("File view", viewFormatToggleGroup) {
                addNameTooltip()
            }
            imagePreviewRadioButton = radiobutton("Image preview", viewFormatToggleGroup) {
                addNameTooltip()
            }
            paddingBottom = 4
        }
        add(infoNode)
    }.also { initialize() }

    init {
        viewFormatProperty.onChange { newFormat ->
            newFormat ?: return@onChange
            controller.changeViewFormat(newFormat)
        }
    }

    fun resetSettings() {
        checkBoxSelectionState = CatalogueController.initialSelectionState
        viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(CatalogueController.initialViewFormat))
    }

    fun displayInfoLabel(withText: String) {
        infoLabel.text = withText
        infoNode.isVisible = true
        infoNode.isManaged = true
        isDisplayingInfoLabel = true
    }

    fun hideInfoLabel() {
        if (!isDisplayingInfoLabel) {
            return
        }

        infoLabel.text = ""
        infoNode.isManaged = false
        infoNode.isVisible = false
        isDisplayingInfoLabel = false
    }

    fun updateCheckBoxView() {
        checkBoxSelectionState = catalogueView.currentSelectionState
    }

    private fun initialize() {
        initializeViewFormatRadioButtons()
        viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(CatalogueController.initialViewFormat))
    }

    private fun getRadioButtonViewFormat(radioButton: RadioButton) = when (radioButton) {
        fileNameViewRadioButton -> ViewFormat.FileName
        imagePreviewRadioButton -> ViewFormat.ImagePreview
        else -> throw IllegalArgumentException("Unexpected view format radio button!")
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
            var currentValue = CatalogueController.initialSelectionState
            override fun getValue(thisRef: Any?, property: KProperty<*>): SelectionState = currentValue
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: SelectionState) {
                currentValue = value
                when (currentValue) {
                    SelectionState.All -> {
                        selectionCheckBox.isIndeterminate = false
                        isSelectionCheckBoxChecked = true
                    }
                    SelectionState.None -> {
                        selectionCheckBox.isIndeterminate = false
                        isSelectionCheckBoxChecked = false
                    }
                    SelectionState.Part -> {
                        selectionCheckBox.isIndeterminate = true
                        isSelectionCheckBoxChecked = false
                    }
                }
            }
        }
}