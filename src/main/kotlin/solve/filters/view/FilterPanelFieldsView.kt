package solve.filters.view

import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell
import io.github.palexdev.materialfx.effects.DepthLevel
import javafx.beans.binding.Bindings
import solve.constants.IconsDeletePath
import solve.constants.IconsEditPath
import solve.filters.model.Filter
import solve.styles.FilterPanelFieldsViewStylesheet
import solve.utils.createHGrowHBox
import solve.utils.imageViewIcon
import solve.utils.loadResourcesImage
import solve.utils.materialfx.mfxCheckListView
import tornadofx.*
import java.util.function.Function

class FilterPanelFieldsView : View() {
    private val editIconImage = loadResourcesImage(IconsEditPath)
    private val deleteIconImage = loadResourcesImage(IconsDeletePath)

    val filtersListView = mfxCheckListView<Filter<Any>> {
        addStylesheet(FilterPanelFieldsViewStylesheet::class)

        depthLevel = DepthLevel.LEVEL0

        cellFactory = Function {
            val cell = MFXCheckListCell(this, it)
            addButtonsToCell(cell)

            return@Function cell
        }

        val itemsNumberProperty = Bindings.size(items)
        prefHeightProperty().bind(itemsNumberProperty.multiply(35.0))

        paddingBottom = 15.0
        paddingLeft = 1.5
        useMaxWidth = true
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
}
