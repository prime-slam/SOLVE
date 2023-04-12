package solve.settings.grid.view

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.controlsfx.control.RangeSlider
import solve.constants.IconsSettingsGridDecrementPath
import solve.constants.IconsSettingsGridIncrementPath
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.settings.createSettingsField
import solve.settings.grid.controller.GridSettingsController
import solve.utils.*
import tornadofx.*

class GridSettingsView: View() {
    private val controller: GridSettingsController by inject()
    private val sceneController: SceneController by inject()
    private var sceneFacade: SceneFacade? = null

    private lateinit var scaleRangeSlider: RangeSlider

    private val columnsNumber: Int
        get() = sceneController.columnsNumber

    override val root = vbox {
        vbox {
            style {
                backgroundColor += Color.WHITE
            }

            vbox {
                minWidth = GridSettingsViewMinWidth

                add(createGridSettingsField("Columns", buildColumnsNumberCounter()))

                scaleRangeSlider = buildScaleRangeSlider()
                add(createGridSettingsField("Scale range", scaleRangeSlider))
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
        Platform.runLater {
            sceneFacade = ServiceLocator.getService()
            addSceneListeners()
        }
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
            GridSettingsSettingWidth
        )

    private fun createSettingLabel(name: String) = label(name) {
        font = Font(GridSettingsLabelFontSize)
        prefWidth = GridSettingsLabelWidth
    }

    private fun buildScaleRangeSlider(): RangeSlider {
        val rangeSlider = RangeSlider(
            SceneController.DefaultMinScale,
            SceneController.DefaultMaxScale,
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

            controller.setSceneMinScale(newMinScale)
        }
        rangeSlider.highValueProperty().onChange { newMaxScale ->
            if (isSmallerThanAllowedDifference()) {
                rangeSlider.highValue = rangeSlider.lowValue + GridSettingsScaleRangeMinDifference
                return@onChange
            }

            controller.setSceneMaxScale(newMaxScale)
        }

        return rangeSlider
    }

    private fun buildColumnsNumberCounter() = hbox(5) {
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
            if (columnsNumber > 1) {
                controller.setSceneColumnsNumber(columnsNumber - 1)
            }
        })
        label(sceneController.columnsNumberProperty) {
            font = Font(GridSettingsColumnsNumberCounterFontSize)
        }
        add(createColumnsNumberButton(IconsSettingsGridIncrementPath) {
            if (columnsNumber < SceneController.MaxColumnsNumber) {
                controller.setSceneColumnsNumber(columnsNumber + 1)
            }
        })
    }

    private fun addSceneListeners() {
        sceneFacade?.lastKeepSettingsLayers?.onChange {
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

        // Minimal allow difference between the min and max scale values.
        private const val GridSettingsScaleRangeMinDifference = 0.1
    }
}
