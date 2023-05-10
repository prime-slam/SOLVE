package solve.filters.settings.view

import io.github.palexdev.materialfx.enums.FloatMode
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import solve.filters.settings.controller.FilterSettingsController
import solve.filters.settings.model.FilterSetting
import solve.filters.settings.view.controls.FilterSettingControl
import solve.filters.settings.view.controls.IndicesStepSettingControl
import solve.filters.settings.view.controls.TimePeriodSettingControl
import solve.filters.settings.view.controls.UIDSettingControl
import solve.project.model.ProjectFrame
import solve.styles.Style
import solve.styles.Style.headerPadding
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

    private lateinit var timePeriodCheckbox: CheckBox
    private lateinit var stepNumberCheckBox: CheckBox
    private lateinit var uidIntegerCheckBox: CheckBox
    private val checkboxes: List<CheckBox> by lazy {
        listOf(timePeriodCheckbox, stepNumberCheckBox, uidIntegerCheckBox)
    }

    private val checkboxToSettingNodeMap = mutableMapOf<CheckBox, Node>()
    private val settingControlsMap = mutableMapOf<Node, FilterSettingControl<*, *>>()

    private val areAllIntegerTextFieldsValid: Boolean
        get() = stepNumberIntegerTextField.isValid && uidIntegerTextField.isValid
    private val selectedSettingCheckboxes = observableListOf<CheckBox>()

    private val filterSettingsContentNode = borderpane {
        top = vbox {
            dialogHeaderLabel("Create new filter") {
                padding = headerPadding
            }
            vbox(10) {
                timePeriodSettingField()
                indicesStepSettingField()
                uidSettingField()

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
                isDisable = selectedSettingCheckboxes.isEmpty()
                selectedSettingCheckboxes.onChange {
                    isDisable = selectedSettingCheckboxes.isEmpty()
                }

                action {
                    val filterSettings = getFilterSettings()
                    println(filterSettings)
                }
            }

            alignment = Pos.CENTER_RIGHT
            BorderPane.setMargin(this, Insets(20.0, 0.0, 0.0, 0.0))
        }
    }
    override val root = filterSettingsContentNode

    fun showDialog(parent: View) {
        val content = MaterialFXDialog.createGenericDialog(root)
        val dialog = MaterialFXDialog.createStageDialog(content, parent.currentStage, parent.root as Pane)
        dialog.isDraggable = false

        dialog.show()
        dialog.centerOnScreen()
    }

    private fun EventTarget.integerTextField(
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

        attachTo(this@FilterSettingsView)
    }

    private fun EventTarget.filterSettingField(name: String, settingNode: Node, op: HBox.() -> Unit = {}) = hbox(10) {
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
        checkboxToSettingNodeMap[checkBox] = settingNode

        hbox {
            add(checkBox)
            label(name) {
                font = Font.font(Style.Font, FilterSettingNameFontSize)
            }

            paddingTop = FilterSettingNonTextFieldPaddingTop
        }
        add(settingNode)
        attachTo(this@filterSettingField, op)
    }

    private fun EventTarget.timePeriodSettingField(): HBox {
        val timePeriodRangeSlider = mfxRangeSlider(0.0, 10.0, 1.0, 9.0) {
            prefWidth = TimeLimitRangeSliderWidth
            paddingTop = FilterSettingNonTextFieldPaddingTop + 2.0
        }
        settingControlsMap[timePeriodRangeSlider] = TimePeriodSettingControl(timePeriodRangeSlider)

        return filterSettingField(
            "Time period",
            timePeriodRangeSlider
        )
    }

    private fun EventTarget.indicesStepSettingField(): HBox {
        stepNumberIntegerTextField = integerTextField("Step must be an integer number", 60.0, 145.0)
        settingControlsMap[stepNumberIntegerTextField] = IndicesStepSettingControl(stepNumberIntegerTextField)

        return filterSettingField(
            "Show every",
            stepNumberIntegerTextField
        ) {
            label("image") {
                font = Font.font(Style.Font, FilterSettingNameFontSize)

                paddingTop = FilterSettingNonTextFieldPaddingTop
            }
        }
    }

    private fun EventTarget.uidSettingField(): HBox {
        uidIntegerTextField = integerTextField("UID must be an integer number", 155.0, 155.0)
        settingControlsMap[uidIntegerTextField] = UIDSettingControl(uidIntegerTextField)

        return filterSettingField(
            "Show images with landmark:",
            uidIntegerTextField
        )
    }

    private fun getFilterSettings(): List<FilterSetting<*>> {
        val enabledCheckboxesSettingNodes = selectedSettingCheckboxes.map { checkboxToSettingNodeMap[it] }
        val enabledSettingsControls = enabledCheckboxesSettingNodes.map { settingControlsMap[it] }

        return enabledSettingsControls.mapNotNull { it?.extrudeFilterSettings() }
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
