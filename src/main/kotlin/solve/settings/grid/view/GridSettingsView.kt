package solve.settings.grid.view

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.controlsfx.control.RangeSlider
import solve.constants.IconsSettingsGridDecrementPath
import solve.constants.IconsSettingsGridIncrementPath
import solve.scene.controller.SceneController
import solve.settings.createSettingsField
import solve.settings.grid.controller.GridSettingsController
import solve.utils.*
import tornadofx.*

class GridSettingsView: View() {
    private val controller: GridSettingsController by inject()

    override val root = vbox {
        vbox {
            style {
                backgroundColor += Color.WHITE
            }

            vbox {
                minWidth = GridSettingsViewMinWidth

                add(createGridSettingsField("Columns", buildColumnsNumberCounter()))
                add(createGridSettingsField("Scale range", buildScaleRangeSlider()))
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

    private fun createGridSettingsField(name: String, settingNode: Node) =
        createSettingsField(
            createSettingLabel(name),
            GridSettingsLabelWidth,
            settingNode,
            GridSettingsSettingWidth
        )

    private fun createSettingLabel(name: String) = label(name) {
        font = Font(GridSettingsLabelFontSize)
        prefWidth = GridSettingsLabelWidth
    }

    private fun buildScaleRangeSlider(): RangeSlider {
        val rangeSlider = RangeSlider(
            GridSettingsController.MinScale,
            GridSettingsController.MaxScale,
            SceneController.DefaultMinScale,
            SceneController.DefaultMaxScale
        )
        rangeSlider.isShowTickLabels = true

        fun isSmallerThanAllowedDifference() = rangeSlider.valuesDifference < GridSettingsScaleRangeMinDifference

        rangeSlider.lowValueProperty().onChange { newMinScale ->
            if (isSmallerThanAllowedDifference()) {
                rangeSlider.lowValue = rangeSlider.highValue - GridSettingsScaleRangeMinDifference
                return@onChange
            }

            controller.setMinScale(newMinScale)
        }
        rangeSlider.highValueProperty().onChange { newMaxScale ->
            if (isSmallerThanAllowedDifference()) {
                rangeSlider.highValue = rangeSlider.lowValue + GridSettingsScaleRangeMinDifference
                return@onChange
            }

            controller.setMaxScale(newMaxScale)
        }

        return rangeSlider
    }

    private fun buildColumnsNumberCounter() = hbox(5) {
        val installedColumnsNumber = SceneController.DefaultColumnsNumber.toProperty()

        fun createColumnsNumberButton(iconPath: String, action: () -> Unit) = button {
            addStylesheet(TransparentScalingButtonStyle::class)

            val iconImage = loadResourcesImage(iconPath)

            action {
                action()
            }

            iconImage ?: return@button
            graphic = createImageViewIcon(iconImage, GridSettingsColumnsNumberButtonsSize)
        }

        add(createColumnsNumberButton(IconsSettingsGridDecrementPath) {
            if (installedColumnsNumber > 1) {
                --installedColumnsNumber.value
                controller.setColumnsNumber(installedColumnsNumber.value)
            }
        })
        label(installedColumnsNumber) {
            font = Font(GridSettingsColumnsNumberCounterFontSize)
        }
        add(createColumnsNumberButton(IconsSettingsGridIncrementPath) {
            if (installedColumnsNumber < SceneController.MaxColumnsNumber) {
                ++installedColumnsNumber.value
                controller.setColumnsNumber(installedColumnsNumber.value)
            }
        })

        padding = createInsetsWithValue(5.0)
    }

    companion object {
        private const val GridSettingsViewMinWidth = 220.0

        private const val GridSettingsLabelFontSize = 16.0

        private const val GridSettingsLabelWidth = 100.0
        private const val GridSettingsSettingWidth = 120.0

        private const val GridSettingsColumnsNumberButtonsSize = 18.0
        private const val GridSettingsColumnsNumberCounterFontSize = 18.0

        // Minimal allow difference between the min and max scale values.
        private const val GridSettingsScaleRangeMinDifference = 0.1
    }
}
