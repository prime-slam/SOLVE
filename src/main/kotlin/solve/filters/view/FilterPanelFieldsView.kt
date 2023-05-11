package solve.filters.view

import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell
import io.github.palexdev.materialfx.effects.DepthLevel
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter
import javafx.application.Platform
import javafx.beans.binding.Bindings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import solve.constants.IconsDeletePath
import solve.constants.IconsEditPath
import solve.filters.controller.FilterPanelController
import solve.styles.FilterPanelFieldsViewStylesheet
import solve.utils.createHGrowHBox
import solve.utils.imageViewIcon
import solve.utils.loadResourcesImage
import solve.utils.materialfx.mfxCheckListView
import tornadofx.*
import java.util.function.Function

class FilterPanelFieldsView : View() {
    private val filterPanelController: FilterPanelController by inject()

    private val editIconImage = loadResourcesImage(IconsEditPath)
    private val deleteIconImage = loadResourcesImage(IconsDeletePath)

    val filtersListView = mfxCheckListView(filterPanelController.model.filters) {
        addStylesheet(FilterPanelFieldsViewStylesheet::class)

        converter = FunctionalStringConverter.to { it.preview }

        depthLevel = DepthLevel.LEVEL0

        cellFactory = Function {
            val cell = MFXCheckListCell(this, it)
            addButtonsToCell(cell)

            return@Function cell
        }

        val itemsNumberProperty = Bindings.size(items)
        prefHeightProperty().bind(itemsNumberProperty.multiply(35.0))

        items.onChange { forceCheckboxesVisualization() }
        Platform.runLater { currentWindow?.widthProperty()?.onChange { forceCheckboxesVisualization() } }

        paddingLeft = 1.5
        useMaxWidth = true
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

    override val root = filtersListView

    private fun <T> addButtonsToCell(cell: MFXCheckListCell<T>) {
        cell.add(createHGrowHBox())
        cell.add(
            hbox {
                hbox {
                    imageViewIcon(editIconImage ?: return@hbox, 24.0) {
                        paddingTop = 3.5
                        paddingRight = 7.0
                    }
                }
                imageViewIcon(deleteIconImage ?: return@hbox, 24.0)

                paddingRight = 20.5
            }
        )
    }

    companion object {
        private const val ListFieldSpawnTimeMillis = 20L
    }
}
