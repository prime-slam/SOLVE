package sliv.tool.catalogue.view

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.geometry.Insets
import javafx.scene.control.CheckBox
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import sliv.tool.catalogue.model.ViewFormat
import tornadofx.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CatalogueSettingsView: View() {
    enum class SelectionState {
        All,
        None,
        Part
    }

    private val viewFormatWrapper = ReadOnlyObjectWrapper(CatalogueView.initialViewFormat)
    val viewFormatProperty: ReadOnlyObjectProperty<ViewFormat> = viewFormatWrapper.readOnlyProperty
    private var viewFormat: ViewFormat
        get() = viewFormatProperty.value
        set(value) {
            viewFormatWrapper.value = value
            viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(value))
        }

    private val catalogueView: CatalogueView by inject()

    private val viewFormatToggleGroup = ToggleGroup()
    private lateinit var fileNameViewRadioButton: RadioButton
    private lateinit var imagePreviewRadioButton: RadioButton

    private val selectionCheckBoxBoolProperty = booleanProperty(false)
    private lateinit var selectionCheckBox: CheckBox
    private var checkBoxSelectionState: SelectionState by checkBoxSelectionStateDelegate()

    private val infoLabel = label()
    private var isDisplayingInfoLabel = false

    override val root = hbox {
        padding = Insets(5.0, 5.0, 5.0, 5.0)
        spacing = 5.0
        selectionCheckBox = checkbox("Select all", selectionCheckBoxBoolProperty)
        pane().hgrow = Priority.ALWAYS
        fileNameViewRadioButton = radiobutton("File view", viewFormatToggleGroup)
        imagePreviewRadioButton = radiobutton("Image preview", viewFormatToggleGroup)
        add(infoLabel)
    }.also { initializeViewFormatRadioButtons() }

    init {
        subscribe<CatalogueFieldsInteractionEvent> {
            checkBoxSelectionState = catalogueView.currentSelectionState
        }
        subscribe<CatalogueResetEvent> {
            resetSettingsView()
        }
    }

    fun displayInfoLabel(withText: String) {
        infoLabel.text = withText
        infoLabel.isVisible = true
        infoLabel.isManaged = true
        isDisplayingInfoLabel = true
    }

    fun hideInfoLabel() {
        if (!isDisplayingInfoLabel) {
            return
        }

        infoLabel.isManaged = false
        infoLabel.isVisible = false
        isDisplayingInfoLabel = false
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
            var currentValue = CatalogueView.initialSelectionState
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

    private fun resetSettingsView() {
        checkBoxSelectionState = CatalogueView.initialSelectionState
        viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(CatalogueView.initialViewFormat))
    }
}