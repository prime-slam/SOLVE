package solve.filters.view

import io.github.palexdev.materialfx.controls.MFXCheckListView
import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell
import io.github.palexdev.materialfx.effects.DepthLevel
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.MapChangeListener
import javafx.scene.control.Label
import javafx.scene.text.Font
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import solve.constants.IconsDeletePath
import solve.constants.IconsEditPath
import solve.filters.controller.FilterPanelController
import solve.filters.model.Filter
import solve.filters.settings.view.FilterSettingsView
import solve.styles.FilterPanelFieldsViewStylesheet
import solve.styles.Style
import solve.utils.createHGrowHBox
import solve.utils.imageViewIcon
import solve.utils.loadResourcesImage
import solve.utils.materialfx.mfxCheckListView
import solve.utils.materialfx.mfxCircleButton
import tornadofx.*
import java.util.function.Function

class FilterPanelFieldsView : View() {
    private val filterPanelController: FilterPanelController by inject()
    private val filterPanelSettingsView: FilterSettingsView by inject()

    private val editIconImage = loadResourcesImage(IconsEditPath)
    private val deleteIconImage = loadResourcesImage(IconsDeletePath)

    val filtersListView = mfxCheckListView(filterPanelController.model.filters) {
        addStylesheet(FilterPanelFieldsViewStylesheet::class)

        converter = FunctionalStringConverter.to { it.preview }

        depthLevel = DepthLevel.LEVEL0

        cellFactory = Function {
            val cell = MFXCheckListCell(this, it)
            initializeCellGraphic(cell)

            return@Function cell
        }

        addFilterListViewBindings()

        paddingLeft = 1.5
        itemsProperty().onChange {
            paddingBottom = if (items.isEmpty()) {
                0.0
            } else {
                14.0
            }
        }
        useMaxWidth = true
    }

    override val root = filtersListView

    private fun initializeCellGraphic(cell: MFXCheckListCell<Filter>) {
        val filter = cell.data

        val cellTextLabel = cell.getChildList()?.firstOrNull { child -> child is Label }
        cellTextLabel?.tooltip {
            text = filter.preview
            font = Font.font(Style.Font)
        }
        cell.add(createHGrowHBox())
        cell.add(
            hbox {
                mfxCircleButton(radius = FieldButtonPressRippleCircleRadius) {
                    graphic = imageViewIcon(editIconImage ?: return@mfxCircleButton, FieldButtonsIconsSize)

                    action {
                        filterPanelSettingsView.showEditingDialog(filter)
                    }
                }
                mfxCircleButton(radius = FieldButtonPressRippleCircleRadius) {
                    imageViewIcon(deleteIconImage ?: return@mfxCircleButton, FieldButtonsIconsSize)
                    action {
                        // Needed because when an unselected element is removed,
                        // the whole selection is recreated with the addition of the remaining elements,
                        // which value changes causes changes of selectionProperty.
                        filtersListView.selectionModel.deselectItem(filter)

                        filterPanelController.removeFilter(filter)
                    }
                }
                paddingRight = 20.5
            }
        )
    }

    // Needed because mfx checkboxes are invisible until the first click and before a window hiding (mfx problem).
    private fun forceCheckboxesVisualization() {
        CoroutineScope(Dispatchers.JavaFx).launch {
            delay(ListFieldSpawnTimeMillis)

            val visualizationForceNode = pane()
            visualizationForceNode.isManaged = false
            add(visualizationForceNode)
            getChildList()?.remove(visualizationForceNode)
        }
    }

    private fun updateItemsSelection() {
        val selection = filtersListView.selectionModel.selection
        filtersListView.items.forEach { item ->
            if (item.enabled && ! selection.containsValue(item)) {
                filtersListView.selectionModel.selectItem(item)
            } else if (!item.enabled && selection.containsValue(item)) {
                filtersListView.selectionModel.deselectItem(item)
            }
        }
    }

    private fun MFXCheckListView<Filter>.addFilterListViewBindings() {
        val itemsNumberProperty = Bindings.size(items)
        prefHeightProperty().bind(itemsNumberProperty.multiply(35.0))

        items.onChange {
            updateItemsSelection()
            forceCheckboxesVisualization()
        }

        Platform.runLater { currentWindow?.widthProperty()?.onChange { forceCheckboxesVisualization() } }

        selectionModel.selectionProperty().addListener(
            MapChangeListener { change ->
                val filter = change.valueAdded ?: change.valueRemoved
                filter.enabled = change.wasAdded()

                filterPanelController.applyFilters()
            }
        )
    }

    companion object {
        private const val ListFieldSpawnTimeMillis = 500L

        private const val FieldLabelFontSize = 16.0
        private const val FieldButtonsIconsSize = 24.0
        private const val FieldButtonPressRippleCircleRadius = 15.0
    }
}
