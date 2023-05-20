package solve.settings.grid.view

import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import org.controlsfx.control.RangeSlider
import solve.constants.IconsSettingsGridDecrementPath
import solve.constants.IconsSettingsGridIncrementPath
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.settings.createSettingsField
import solve.settings.grid.controller.GridSettingsController
import solve.styles.Style
import solve.utils.*
import tornadofx.*
import solve.utils.materialfx.mfxCircleButton
import solve.utils.materialfx.mfxRangeSlider
import kotlin.math.roundToLong

class GridSettingsView : View() {
    private val controller: GridSettingsController by inject()
    private val sceneController: SceneController by inject()

    private var scaleRangeSlider = RangeSlider()

    private val columnsNumber: Int
        get() = sceneController.installedColumnsNumber

    override val root = vbox {
        vbox {
            style {
                backgroundColor += Paint.valueOf(Style.SurfaceColor)
            }

            vbox {
                minWidth = GridSettingsViewMinWidth

                add(createGridSettingsField("Columns", buildColumnsNumberCounter()))
                add(createGridSettingsField("Scale range", hbox()))
                add(buildScaleRangeSlider())
            }
            border = Border(
                BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
            )
            padding = Insets(8.0, 10.0, 8.0, 10.0)
            vgrow = Priority.ALWAYS
        }
        padding = createInsetsWithValue(5.0)
        vgrow = Priority.ALWAYS
    }

    init {
        addSceneListeners()
    }

    private fun setDefaultScaleRangeSliderValues() {
        scaleRangeSlider.lowValue = scaleRangeSlider.min
        scaleRangeSlider.highValue = scaleRangeSlider.max
    }

    private fun createGridSettingsField(name: String, settingNode: Node) =
        createSettingsField(
            createSettingLabel(name),
            GridSettingsLabelWidth,
            settingNode,
            GridSettingsSettingWidth,
            isLabelOnLeft = true
        )

    private fun createSettingLabel(name: String) = label(name) {
        font = Font(GridSettingsLabelFontSize)
        prefWidth = GridSettingsLabelWidth
    }


    private fun EventTarget.settingFieldLabel(
        text: String = "",
        fontSize: Double = 14.0,
        op: Label.() -> Unit = {}
    ) = label(text) {
        font = Font.font(Style.Font, fontSize)

        attachTo(this@settingFieldLabel, op)
    }

    private fun createIntegerRangeSliderRangeInfoString(rangeSlider: RangeSlider): String {
        val fromValue = rangeSlider.lowValue.roundToLong()
        val toValue = rangeSlider.highValue.roundToLong()

        return "$fromValue - $toValue"
    }

    private fun buildScaleRangeSlider(): Node {
        val rangeSlider =
            mfxRangeSlider(0.2, 20.0, 0.2, 20.0) {
            }
        rangeSlider.prefWidth = 130.0

        fun isSmallerThanAllowedDifference() = rangeSlider.valuesDifference < GridSettingsScaleRangeMinDifference

        rangeSlider.lowValueProperty().onChange { newMinScale ->
            if (isSmallerThanAllowedDifference()) {
                rangeSlider.lowValue = rangeSlider.highValue - GridSettingsScaleRangeMinDifference
                return@onChange
            }

            controller.setSceneMinScale(newMinScale)
        }
        rangeSlider.highValueProperty().onChange { newMaxScale ->
            if (isSmallerThanAllowedDifference()) {
                rangeSlider.highValue = rangeSlider.lowValue + GridSettingsScaleRangeMinDifference
                return@onChange
            }

            controller.setSceneMaxScale(newMaxScale)
        }

        val scaleRangeSettingNode = stackpane {
            hbox {
                add(createHGrowHBox())
                settingFieldLabel() {
                    rangeSlider.lowValueProperty().onChange {
                        text = createIntegerRangeSliderRangeInfoString(rangeSlider)
                    }
                    rangeSlider.highValueProperty().onChange {
                        text = createIntegerRangeSliderRangeInfoString(rangeSlider)
                    }
                }
                add(createHGrowHBox())

                paddingBottom = 30.0
            }
            add(rangeSlider)

            paddingBottom = 8.0
            paddingLeft = 5.0
        }

        return scaleRangeSettingNode
    }

    private fun createColumnsNumberButton(
        iconPath: String,
        isActiveProperty: SimpleObjectProperty<Boolean>,
        action: () -> Unit
    ): Node =
        mfxCircleButton(radius = 12.0) {
            style {
                backgroundColor += Color.TRANSPARENT
            }

            val iconImage = loadResourcesImage(iconPath)

            action {
                if (isActiveProperty.value) {
                    action()
                }
            }

            fun updateViewByActivity(isActive: Boolean) {
                if (!isActive) {
                    unscale()
                    graphic.opacity = GridSettingsColumnsNumberButtonInactiveOpacity
                } else {
                    graphic.opacity = 1.0
                }
            }

            iconImage ?: return@mfxCircleButton
            graphic = imageViewIcon(iconImage, GridSettingsColumnsNumberButtonsSize)
            updateViewByActivity(isActiveProperty.value)

            isActiveProperty.onChange { isActive ->
                isActive ?: return@onChange

                updateViewByActivity(isActive)
            }
        }

    private fun buildColumnsNumberCounter() = hbox(5) {
        val isDecrementActive = SimpleObjectProperty(false)
        val isIncrementActive = SimpleObjectProperty(false)
        sceneController.installedColumnsNumberProperty.onChange { columnsNumber ->
            isDecrementActive.value = columnsNumber > 1
            isIncrementActive.value = columnsNumber < SceneController.MaxColumnsNumber
        }

        add(
            createColumnsNumberButton(IconsSettingsGridDecrementPath, isDecrementActive) {
                if (columnsNumber > 1) {
                    controller.setSceneColumnsNumber(columnsNumber - 1)
                }
            }
        )
        label(sceneController.installedColumnsNumberProperty) {
            font = Font(GridSettingsColumnsNumberCounterFontSize)
        }
        add(
            createColumnsNumberButton(IconsSettingsGridIncrementPath, isIncrementActive) {
                if (columnsNumber < SceneController.MaxColumnsNumber) {
                    controller.setSceneColumnsNumber(columnsNumber + 1)
                }
            }
        )
    }

    private fun addSceneListeners() {
        SceneFacade.lastVisualizationKeepSettingsProperty.onChange {
            setDefaultScaleRangeSliderValues()
        }
    }

    companion object {
        private const val GridSettingsViewMinWidth = 220.0

        private const val GridSettingsLabelFontSize = 16.0

        private const val GridSettingsLabelWidth = 100.0
        private const val GridSettingsSettingWidth = 120.0

        private const val GridSettingsColumnsNumberButtonsSize = 18.0
        private const val GridSettingsColumnsNumberCounterFontSize = 18.0
        private const val GridSettingsColumnsNumberButtonInactiveOpacity = 0.6

        // Minimal allow difference between the min and max scale values.
        private const val GridSettingsScaleRangeMinDifference = 0.1
    }
}
