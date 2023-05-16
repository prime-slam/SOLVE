package solve.filters.settings.view

import io.github.palexdev.materialfx.enums.FloatMode
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import org.controlsfx.control.RangeSlider
import solve.filters.model.Filter
import solve.filters.settings.controller.FilterSettingsController
import solve.filters.settings.model.FilterSetting
import solve.filters.settings.view.connectors.FilterSettingNodeConnector
import solve.filters.settings.view.connectors.IndicesStepSettingNodeConnector
import solve.filters.settings.view.connectors.TimePeriodSettingNodeConnector
import solve.filters.settings.view.connectors.UIDSettingNodeConnector
import solve.styles.Style
import solve.styles.Style.headerPadding
import solve.utils.createHGrowHBox
import solve.utils.getKeys
import solve.utils.materialfx.MFXIntegerTextField
import solve.utils.materialfx.MFXIntegerTextField.Companion.mfxIntegerTextField
import solve.utils.materialfx.MaterialFXDialog
import solve.utils.materialfx.controlButton
import solve.utils.materialfx.dialogHeaderLabel
import solve.utils.materialfx.mfxCheckbox
import solve.utils.materialfx.mfxIntegerRangeSlider
import tornadofx.*
import kotlin.math.roundToLong

class FilterSettingsView : View() {
    private val controller: FilterSettingsController by inject()

    private lateinit var stepNumberIntegerTextField: MFXIntegerTextField
    private lateinit var uidIntegerTextField: MFXIntegerTextField

    private var currentSettingsDialogModeProperty = SimpleObjectProperty(FilterSettingsDialogMode.CreationMode)
    private var currentSettingsDialogMode by currentSettingsDialogModeProperty

    private val currentHeaderText: String
        get() = if (currentSettingsDialogMode == FilterSettingsDialogMode.CreationMode) {
            CreationModeDialogHeaderText
        } else {
            EditingModeDialogHeaderText
        }
    private val currentOKButtonText: String
        get() = if (currentSettingsDialogMode == FilterSettingsDialogMode.CreationMode) {
            CreationModeDialogOKButtonText
        } else {
            EditingModeDialogOKButtonText
        }

    lateinit var editingModeOldFilter: Filter

    private val checkboxToSettingNodeMap = mutableMapOf<CheckBox, Node>()
    private val nodeToNodeConnectorMap =
        mutableMapOf<Node, FilterSettingNodeConnector<out Node, out FilterSetting<out Any>>>()

    private val validTextFields = observableSetOf<TextField>()
    private val enabledTextFields = observableSetOf<TextField>()
    private val notEmptyTextFields = observableSetOf<TextField>()

    private val settingCheckboxes = mutableListOf<CheckBox>()
    private val selectedSettingCheckboxes = observableSetOf<CheckBox>()

    private val areAllEnabledTextFieldsValid: Boolean
        get() = validTextFields.containsAll(enabledTextFields)
    private val haveSelectedCheckboxes: Boolean
        get() = selectedSettingCheckboxes.isNotEmpty()
    private val areAllEnabledTextFieldsNotEmpty: Boolean
        get() = notEmptyTextFields.containsAll(enabledTextFields)
    private val canCreateFilter: Boolean
        get() = haveSelectedCheckboxes && areAllEnabledTextFieldsValid && areAllEnabledTextFieldsNotEmpty

    private val filterSettingsContentNode = borderpane {
        top = vbox {
            dialogHeaderLabel(currentHeaderText) {
                currentSettingsDialogModeProperty.onChange {
                    text = currentHeaderText
                }

                padding = headerPadding
            }
            vbox(10) {
                add(buildTimePeriodSettingNode())
                add(buildIndicesStepSettingNode())
                add(buildUIDSettingField())

                paddingTop = 20.0
                paddingLeft = 30.0
            }

            paddingRight = 20.0
        }
        bottom = hbox(Style.ControlButtonsSpacing) {
            controlButton("CANCEL") {
                action {
                    this@FilterSettingsView.close()
                }
            }
            controlButton(currentOKButtonText) {
                currentSettingsDialogModeProperty.onChange {
                    text = currentOKButtonText
                }

                fun updateDisableProperty() {
                    isDisable = !canCreateFilter
                }

                updateDisableProperty()
                val updateDisablePropertyChangeListener = InvalidationListener { updateDisableProperty() }
                validTextFields.addListener(updateDisablePropertyChangeListener)
                enabledTextFields.addListener(updateDisablePropertyChangeListener)
                selectedSettingCheckboxes.addListener(updateDisablePropertyChangeListener)
                notEmptyTextFields.addListener(updateDisablePropertyChangeListener)

                action {
                    onCreateFilter()
                    this@FilterSettingsView.close()
                }
            }

            alignment = Pos.CENTER_RIGHT
            BorderPane.setMargin(this, Insets(20.0, 0.0, 0.0, 0.0))
        }
    }
    override val root = filterSettingsContentNode

    fun showCreationDialog() {
        currentSettingsDialogMode = FilterSettingsDialogMode.CreationMode
        setDialogInitialState()
        initializeAndShowDialog(this)
    }

    fun showEditingDialog(oldFilter: Filter) {
        currentSettingsDialogMode = FilterSettingsDialogMode.EditingMode
        editingModeOldFilter = oldFilter

        setDialogInitialState()
        setControlNodesSettingsFromFilter(editingModeOldFilter)
        enableControlNodesCheckboxesFromFilter(editingModeOldFilter)

        initializeAndShowDialog(this)
    }

    private fun onCreateFilter() {
        val filterSettings = getFilterSettings()

        if (currentSettingsDialogMode == FilterSettingsDialogMode.CreationMode) {
            controller.createFilter(filterSettings)
        } else {
            controller.editFilter(editingModeOldFilter, filterSettings)
        }
    }

    private fun initializeAndShowDialog(parent: View) {
        val content = MaterialFXDialog.createGenericDialog(root)
        val dialog = MaterialFXDialog.createStageDialog(content, parent.currentStage, parent.root as Pane)
        dialog.isDraggable = false

        dialog.show()
        dialog.centerOnScreen()
    }

    private fun setDialogInitialState() {
        settingCheckboxes.forEach { it.isSelected = false }
        nodeToNodeConnectorMap.forEach { (node, connector) ->
            connector.setDefaultSettingNodeState(node)
        }
        selectedSettingCheckboxes.clear()
        validTextFields.clear()
        enabledTextFields.clear()
        notEmptyTextFields.clear()
    }

    private fun getNodeByFilterSetting(setting: FilterSetting<out Any>): Node {
        val correspondingSettingControl = nodeToNodeConnectorMap.values.first {
            it.filterSettingKCLass == setting::class
        }

        return nodeToNodeConnectorMap.getKeys(correspondingSettingControl).first()
    }

    private fun setControlNodesSettingsFromFilter(filter: Filter) {
        filter.settings.forEach { setting ->
            val correspondingNode = getNodeByFilterSetting(setting)
            val correspondingControl = nodeToNodeConnectorMap[correspondingNode]

            correspondingControl?.updateSettingNodeWithSettings(correspondingNode, setting)
        }
    }

    private fun enableControlNodesCheckboxesFromFilter(filter: Filter) {
        filter.settings.forEach { setting ->
            val correspondingNode = getNodeByFilterSetting(setting)
            checkboxToSettingNodeMap.getKeys(correspondingNode).first().isSelected = true
        }
    }

    private fun Node.setEnabledByCheckboxSelection(checkBox: CheckBox) {
        enableWhen(checkBox.selectedProperty())
    }

    private fun buildIntegerTextField(
        notIntegerErrorMessage: String,
        width: Double,
        maxWidth: Double
    ) = mfxIntegerTextField(notIntegerErrorMessage) {
        style {
            fontFamily = Style.Font
        }
        minWidth = width
        prefWidth = width
        this.maxWidth = maxWidth

        textLimit = IntegerTextFieldSymbolsLimit
        floatMode = FloatMode.ABOVE

        disableProperty().onChange { isDisable ->
            if (isDisable) {
                text = ""
                isAllowEdit = false
                enabledTextFields.remove(this)
            } else {
                isAllowEdit = true
                enabledTextFields.add(this)
            }
        }
        textProperty().onChange {
            if (text.isEmpty()) {
                notEmptyTextFields.remove(this)
            } else {
                notEmptyTextFields.add(this)
            }

            if (!isValid) {
                validTextFields.remove(this)
            } else {
                validTextFields.add(this)
            }
        }

        alignment = Pos.CENTER
    }

    private fun EventTarget.settingFieldLabel(
        text: String = "",
        fieldCheckbox: CheckBox,
        fontSize: Double = FilterSettingNameFontSize,
        op: Label.() -> Unit = {}
    ) = label(text) {
        font = Font.font(Style.Font, fontSize)
        setEnabledByCheckboxSelection(fieldCheckbox)

        attachTo(this@settingFieldLabel, op)
    }

    private fun createSettingField(
        name: String,
        checkBox: CheckBox,
        settingNode: Node,
        activeNode: Node = settingNode
    ): HBox {
        activeNode.setEnabledByCheckboxSelection(checkBox)

        val settingFieldNode = hbox(10) {
            checkboxToSettingNodeMap[checkBox] = activeNode
            hbox {
                add(checkBox)
                settingFieldLabel(name, checkBox) {
                    alignment = Pos.BOTTOM_CENTER
                }

                paddingTop = FilterSettingNonTextFieldPaddingTop
            }
            add(settingNode)
        }

        return settingFieldNode
    }

    private fun createSettingFieldCheckbox(): CheckBox {
        val checkbox = mfxCheckbox {
            paddingTop = -7.0

            selectedProperty().onChange { selected ->
                if (selected) {
                    selectedSettingCheckboxes.add(this)
                } else {
                    selectedSettingCheckboxes.remove(this)
                }
            }
        }
        settingCheckboxes.add(checkbox)

        return checkbox
    }

    private fun createIntegerRangeSliderRangeInfoString(rangeSlider: RangeSlider): String {
        val fromValue = rangeSlider.lowValue.roundToLong()
        val toValue = rangeSlider.highValue.roundToLong()

        return "$fromValue - $toValue"
    }

    private fun buildTimePeriodSettingNode(): HBox {
        val timePeriodRangeSlider = mfxIntegerRangeSlider(0.0, 1.0, 0.0, 1.0) {
            prefWidth = TimeLimitRangeSliderWidth
            paddingTop = FilterSettingNonTextFieldPaddingTop + 2.0
        }
        nodeToNodeConnectorMap[timePeriodRangeSlider] = TimePeriodSettingNodeConnector

        val fieldCheckbox = createSettingFieldCheckbox()
        val timePeriodSettingNode = stackpane {
            hbox {
                add(createHGrowHBox())
                settingFieldLabel(fieldCheckbox = fieldCheckbox) {
                    visibleWhen(fieldCheckbox.selectedProperty())
                    timePeriodRangeSlider.lowValueProperty().onChange {
                        text = createIntegerRangeSliderRangeInfoString(timePeriodRangeSlider)
                    }
                    timePeriodRangeSlider.highValueProperty().onChange {
                        text = createIntegerRangeSliderRangeInfoString(timePeriodRangeSlider)
                    }
                }
                add(createHGrowHBox())

                paddingBottom = 30.0
            }
            add(timePeriodRangeSlider)

            paddingBottom = 8.0
            paddingLeft = 5.0
        }

        return createSettingField(
            "Time period:",
            fieldCheckbox,
            timePeriodSettingNode,
            timePeriodRangeSlider
        )
    }

    private fun buildIndicesStepSettingNode(): HBox {
        stepNumberIntegerTextField = buildIntegerTextField("Step must be an integer number", 60.0, 145.0)
        nodeToNodeConnectorMap[stepNumberIntegerTextField] = IndicesStepSettingNodeConnector

        val fieldCheckbox = createSettingFieldCheckbox()

        val fieldNode = createSettingField(
            "Show every",
            fieldCheckbox,
            stepNumberIntegerTextField.root,
            stepNumberIntegerTextField
        )
        fieldNode.add(
            settingFieldLabel("image", fieldCheckbox) {
                paddingTop = FilterSettingNonTextFieldPaddingTop
            }
        )

        return fieldNode
    }

    private fun buildUIDSettingField(): HBox {
        uidIntegerTextField = buildIntegerTextField("UID must be an integer number", 155.0, 155.0)
        nodeToNodeConnectorMap[uidIntegerTextField] = UIDSettingNodeConnector

        return createSettingField(
            "Show images with landmark:",
            createSettingFieldCheckbox(),
            uidIntegerTextField.root,
            uidIntegerTextField
        )
    }

    private fun getFilterSettings(): List<FilterSetting<out Any>> {
        val enabledSettingNodes = selectedSettingCheckboxes.map { checkboxToSettingNodeMap[it] }

        return enabledSettingNodes.mapNotNull { settingNode ->
            settingNode ?: return@mapNotNull null

            val correspondingControl = nodeToNodeConnectorMap[settingNode]

            return@mapNotNull correspondingControl?.extractFilterSettings(settingNode)
        }
    }

    companion object {
        private const val IntegerTextFieldSymbolsLimit = Long.MIN_VALUE.toString().length

        private const val CreationModeDialogHeaderText = "Create new filter"
        private const val EditingModeDialogHeaderText = "Edit filter"
        private const val CreationModeDialogOKButtonText = "CREATE"
        private const val EditingModeDialogOKButtonText = "SAVE"

        private const val FilterSettingNameFontSize = 14.0
        private const val FilterSettingNonTextFieldPaddingTop = 8.0
        private const val TimeLimitRangeSliderWidth = 300.0
    }
}

private enum class FilterSettingsDialogMode {
    CreationMode,
    EditingMode
}
