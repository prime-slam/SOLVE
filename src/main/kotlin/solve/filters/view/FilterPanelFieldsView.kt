package solve.filters.view

import io.github.palexdev.materialfx.controls.MFXCheckListView
import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell
import io.github.palexdev.materialfx.effects.DepthLevel
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.MapChangeListener
import javafx.scene.control.CheckBox
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

    private val filtersListView = mfxCheckListView(observableListOf<Filter>()) {
        addStylesheet(FilterPanelFieldsViewStylesheet::class)

        converter = FunctionalStringConverter.to { it.preview }

        depthLevel = DepthLevel.LEVEL0

        cellFactory = Function {
            val cell = MFXCheckListCell(this, it)
            initializeCellGraphic(cell, it)

            return@Function cell
        }

        addFilterListViewBindings()

        paddingLeft = 1.5
        useMaxWidth = true
    }

    override val root = filtersListView

    private fun initializeCellGraphic(cell: MFXCheckListCell<Filter>, filter: Filter) {
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
        Platform.runLater {
            val cellCheckbox = (cell.getChildList()?.firstOrNull { it is CheckBox } as? CheckBox) ?: return@runLater
            cellCheckbox.isSelected = filter.enabled
        }
    }

    private fun MFXCheckListView<Filter>.addFilterListViewBindings() {
        val itemsNumberProperty = Bindings.size(items)
        prefHeightProperty().bind(itemsNumberProperty.multiply(35.0))

        selectionModel.selectionProperty().addListener(
            MapChangeListener { change ->
                val filter = change.valueAdded ?: change.valueRemoved
                filter.enabled = change.wasAdded()
                filterPanelController.applyFilters()
            }
        )

        // Needed because mfx list view does not work correct in some cases.
        // When an element is replaced in the filter list (deletion and addition),
        // the mfx list skips the operation of deletion of a replaced element,
        // but adds a new element and deletes the first element in the list.
        // Also, mfx library fully reinitialize list during the addition and removing,
        // so it causes calls of selectionProperty changes and checkboxes selection state is incorrect.
        filterPanelController.model.filters.onChange { change ->
            while (change.next()) {
                CoroutineScope(Dispatchers.JavaFx).launch {
                    change.removed.forEach {
                        if (it.enabled && selectionModel.selection.containsValue(it)) {
                            selectionModel.deselectItem(it)
                        }
                    }
                    filtersListView.items.removeAll(change.removed)

                    delay(DelayBetweenMFXListViewChangeOperationsMillis)

                    filtersListView.items.addAll(change.addedSubList)
                }
            }
        }
    }

    companion object {
        private const val FieldButtonsIconsSize = 24.0
        private const val FieldButtonPressRippleCircleRadius = 15.0
        private const val DelayBetweenMFXListViewChangeOperationsMillis = 50L
    }
}