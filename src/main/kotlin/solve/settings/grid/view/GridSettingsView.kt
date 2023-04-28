package solve.settings.grid.view

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.controlsfx.control.RangeSlider
import solve.constants.IconsSettingsGridDecrementPath
import solve.constants.IconsSettingsGridIncrementPath
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.settings.createSettingsField
import solve.settings.grid.controller.GridSettingsController
import solve.styles.RangeSliderStylesheet
import solve.styles.Style
import solve.utils.createInsetsWithValue
import solve.utils.imageViewIcon
import solve.utils.loadResourcesImage
import solve.utils.scale
import solve.utils.unscale
import solve.utils.valuesDifference
import tornadofx.*

class GridSettingsView : View() {
    private val controller: GridSettingsController by inject()
    private val sceneController: SceneController by inject()

    private lateinit var scaleRangeSlider: RangeSlider

    private val columnsNumber: Int
        get() = sceneController.installedColumnsNumber

    override val root = vbox {
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed")
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed:wght@700")
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto")
        addStylesheet(RangeSliderStylesheet::class)
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

    private fun createColumnsNumberButton(
        iconPath: String,
        isActiveProperty: SimpleObjectProperty<Boolean>,
        action: () -> Unit
    ) = button {
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

        iconImage ?: return@button
        graphic = imageViewIcon(iconImage, GridSettingsColumnsNumberButtonsSize)
        updateViewByActivity(isActiveProperty.value)

        isActiveProperty.onChange { isActive ->
            isActive ?: return@onChange

            updateViewByActivity(isActive)
        }
        setOnMouseEntered {
            if (isActiveProperty.value) {
                scale(GridSettingsColumnsNumberButtonHoveredScale)
            }
        }
        setOnMouseExited {
            unscale()
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
        private const val GridSettingsViewMinWidth = 300.0

        private const val GridSettingsLabelFontSize = 14.0

        private const val GridSettingsLabelWidth = 100.0
        private const val GridSettingsSettingWidth = 200.0

        private const val GridSettingsColumnsNumberButtonsSize = 18.0
        private const val GridSettingsColumnsNumberCounterFontSize = 18.0
        private const val GridSettingsColumnsNumberButtonHoveredScale = 1.25
        private const val GridSettingsColumnsNumberButtonInactiveOpacity = 0.6

        // Minimal allow difference between the min and max scale values.
        private const val GridSettingsScaleRangeMinDifference = 0.1
    }
}
