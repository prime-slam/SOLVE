package solve.catalogue.view

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import org.controlsfx.control.SegmentedButton
import solve.catalogue.controller.CatalogueController
import solve.catalogue.model.ViewFormat
import solve.styles.CatalogueViewStylesheet
import solve.styles.Style
import solve.utils.materialfx.mfxCheckbox
import tornadofx.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CatalogueSettingsView : View() {
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
    private lateinit var selectionCheckBox: CheckBox
    private var checkBoxSelectionState: SelectionState by checkBoxSelectionStateDelegate()

    private val infoLabel = label()
    private val infoNode = vbox {
        separator()
        add(infoLabel)
        alignment = Pos.CENTER
    }
    private var isDisplayingInfoLabel = false

    private var fileNameViewRadioButton = ToggleButton("FILES").apply {
        style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-weight: ${Style.FontWeightBold};"
    }
    private var imagePreviewRadioButton = ToggleButton("IMAGES").apply {
        style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-weight: ${Style.FontWeightBold};"
    }

    private var segmentedButton = SegmentedButton(fileNameViewRadioButton, imagePreviewRadioButton)

    override val root =
        vbox {
            hbox(130) {
                addStylesheet(CatalogueViewStylesheet::class)
                maxWidth = 500.0
                paddingAll = 0.0
                selectionCheckBox = mfxCheckbox {
                    paddingLeft = 7.0
                    action {
                        if (isSelected) {
                            controller.checkAllFields()
                        } else {
                            controller.uncheckAllFields()
                        }
                    }
                }
                segmentedButton.toggleGroup = viewFormatToggleGroup

                add(segmentedButton)
                paddingBottom = 4
            }
        }.also { initialize() }

    init {
        viewFormatProperty.onChange { newFormat ->
            newFormat ?: return@onChange
            controller.changeViewFormat(newFormat)
        }
    }

    fun resetSettings() {
        checkBoxSelectionState = CatalogueController.initialSelectionState
        viewFormatToggleGroup.selectToggle(getViewFormatToggleButton(CatalogueController.initialViewFormat))
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
        viewFormatToggleGroup.selectToggle(getViewFormatToggleButton(CatalogueController.initialViewFormat))
    }

    private fun getRadioButtonViewFormat(toggleButton: ToggleButton) = when (toggleButton) {
        fileNameViewRadioButton -> ViewFormat.FileName
        imagePreviewRadioButton -> ViewFormat.ImagePreview
        else -> throw IllegalArgumentException("Unexpected view format toggle button!")
    }

    private fun getViewFormatToggleButton(viewFormat: ViewFormat) = when (viewFormat) {
        ViewFormat.FileName -> fileNameViewRadioButton
        ViewFormat.ImagePreview -> imagePreviewRadioButton
    }

    private fun initializeViewFormatRadioButtons() {
        viewFormatToggleGroup.selectedToggleProperty().onChange {
            it ?: return@onChange
            viewFormat = getRadioButtonViewFormat(it as ToggleButton)
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
