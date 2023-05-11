package solve.filters.settings.view

import io.github.palexdev.materialfx.enums.FloatMode
import javafx.beans.InvalidationListener
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
import solve.filters.model.Filter
import solve.filters.settings.controller.FilterSettingsController
import solve.filters.settings.model.FilterSetting
import solve.filters.settings.view.controls.FilterSettingNodeConnector
import solve.filters.settings.view.controls.IndicesStepSettingNodeConnector
import solve.filters.settings.view.controls.TimePeriodSettingNodeConnector
import solve.filters.settings.view.controls.UIDSettingNodeConnector
import solve.project.model.ProjectFrame
import solve.styles.Style
import solve.styles.Style.headerPadding
import solve.utils.getKeys
import solve.utils.materialfx.MFXIntegerTextField
import solve.utils.materialfx.MFXIntegerTextField.Companion.mfxIntegerTextField
import solve.utils.materialfx.MaterialFXDialog
import solve.utils.materialfx.controlButton
import solve.utils.materialfx.dialogHeaderLabel
import solve.utils.materialfx.mfxCheckbox
import solve.utils.materialfx.mfxRangeSlider
import tornadofx.*

class FilterSettingsView : View() {
    private val controller: FilterSettingsController by inject()

    private lateinit var stepNumberIntegerTextField: MFXIntegerTextField
    private lateinit var uidIntegerTextField: MFXIntegerTextField

    private val checkboxToSettingNodeMap = mutableMapOf<CheckBox, Node>()
    private val nodeToNodeConnectorMap =
        mutableMapOf<Node, FilterSettingNodeConnector<out Node, out FilterSetting<out Any>>>()

    private val validTextFields = observableSetOf<TextField>()
    private val enabledTextFields = observableSetOf<TextField>()
    private val notEmptyTextFields = observableSetOf<TextField>()

    private val settingCheckboxes = mutableListOf<CheckBox>()
    private val selectedSettingCheckboxes = observableSetOf<CheckBox>()

    private val areAllEnabledTextFieldsValid: Boolean
        get() = validTextFields.toSet() == enabledTextFields.toSet()
    private val haveSelectedCheckboxes: Boolean
        get() = selectedSettingCheckboxes.isNotEmpty()
    private val areAllEnabledTextFieldsNotEmpty: Boolean
        get() = validTextFields.toSet() == notEmptyTextFields.toSet()
    private val canCreateFilter: Boolean
        get() = haveSelectedCheckboxes && areAllEnabledTextFieldsValid && areAllEnabledTextFieldsNotEmpty

    private val filterSettingsContentNode = borderpane {
        top = vbox {
            dialogHeaderLabel("Create new filter") {
                padding = headerPadding
            }
            vbox(10) {
                add(buildTimePeriodSettingNode())
                add(buildIndicesStepSettingNode())
                add(buildUIDSettingField())

                paddingTop = 10.0
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
            controlButton("CREATE") {
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
                    val filterSettings = getFilterSettings()
                    controller.createFilter(filterSettings)
                    this@FilterSettingsView.close()
                }
            }

            alignment = Pos.CENTER_RIGHT
            BorderPane.setMargin(this, Insets(20.0, 0.0, 0.0, 0.0))
        }
    }
    override val root = filterSettingsContentNode

    fun showCreationDialog(parent: View) {
        setDialogInitialState()

        val content = MaterialFXDialog.createGenericDialog(root)
        val dialog = MaterialFXDialog.createStageDialog(content, parent.currentStage, parent.root as Pane)
        dialog.isDraggable = false

        dialog.show()
        dialog.centerOnScreen()
    }

    fun showEditingDialog(parent: View, oldFilter: Filter) {

    }

    private fun setDialogInitialState() {
        settingCheckboxes.forEach { it.isSelected = false }
        nodeToNodeConnectorMap.forEach { (node, connector) ->
            connector.setDefaultSettingNodeState(node)
        }
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
    }

    private fun EventTarget.settingLabel(
        text: String,
        settingCheckBox: CheckBox,
        op: Label.() -> Unit = {}
    ) = label(text) {
        font = Font.font(Style.Font, FilterSettingNameFontSize)
        setEnabledByCheckboxSelection(settingCheckBox)

        attachTo(this@settingLabel, op)
    }

    private fun createSettingField(
        name: String,
        settingNode: Node,
        activeNode: Node = settingNode
    ): SettingFieldData {
        val checkBox = mfxCheckbox {
            paddingTop = -7.0

            selectedProperty().onChange { selected ->
                if (selected) {
                    selectedSettingCheckboxes.add(this)
                } else {
                    selectedSettingCheckboxes.remove(this)
                }
            }
        }
        settingCheckboxes.add(checkBox)

        activeNode.setEnabledByCheckboxSelection(checkBox)
        val settingFieldNode = hbox(10) {
            checkboxToSettingNodeMap[checkBox] = activeNode
            hbox {
                add(checkBox)
                settingLabel(name, checkBox)

                paddingTop = FilterSettingNonTextFieldPaddingTop
            }
            add(settingNode)
        }

        return SettingFieldData(settingFieldNode, checkBox)
    }

    private fun buildTimePeriodSettingNode(): HBox {
        val timePeriodRangeSlider = mfxRangeSlider(0.0, 10.0, 1.0, 9.0) {
            prefWidth = TimeLimitRangeSliderWidth
            paddingTop = FilterSettingNonTextFieldPaddingTop + 2.0
        }
        nodeToNodeConnectorMap[timePeriodRangeSlider] = TimePeriodSettingNodeConnector

        return createSettingField("Time period:", timePeriodRangeSlider).node
    }

    private fun buildIndicesStepSettingNode(): HBox {
        stepNumberIntegerTextField = buildIntegerTextField("Step must be an integer number", 60.0, 145.0)
        nodeToNodeConnectorMap[stepNumberIntegerTextField] = IndicesStepSettingNodeConnector

        val fieldData = createSettingField("Show every", stepNumberIntegerTextField.root, stepNumberIntegerTextField)
        fieldData.node.add(
            settingLabel("image", fieldData.checkBox) {
                paddingTop = FilterSettingNonTextFieldPaddingTop
            }
        )

        return fieldData.node
    }

    private fun buildUIDSettingField(): HBox {
        uidIntegerTextField = buildIntegerTextField("UID must be an integer number", 155.0, 155.0)
        nodeToNodeConnectorMap[uidIntegerTextField] = UIDSettingNodeConnector

        return createSettingField("Show images with landmark:", uidIntegerTextField.root, uidIntegerTextField).node
    }

    private fun getFilterSettings(): List<FilterSetting<out Any>> {
        val enabledSettingNodes = selectedSettingCheckboxes.map { checkboxToSettingNodeMap[it] }

        return enabledSettingNodes.mapNotNull { settingNode ->
            settingNode ?: return@mapNotNull null

            val correspondingControl = nodeToNodeConnectorMap[settingNode]

            return@mapNotNull correspondingControl?.extractFilterSettings(settingNode)
        }
    }

    private fun getFramesMinTimestamp(frames: List<ProjectFrame>) = frames.minOf { it.timestamp }

    private fun getFramesMaxTimestamp(frames: List<ProjectFrame>) = frames.maxOf { it.timestamp }

    companion object {
        private const val IntegerTextFieldSymbolsLimit = Long.MIN_VALUE.toString().length

        private const val FilterSettingNameFontSize = 14.0
        private const val FilterSettingNonTextFieldPaddingTop = 8.0
        private const val TimeLimitRangeSliderWidth = 300.0
    }
}

private data class SettingFieldData(val node: HBox, val checkBox: CheckBox)
