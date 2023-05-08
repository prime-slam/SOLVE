package solve.filters.view

import javafx.scene.paint.Paint
import solve.constants.IconsFiltersAddPath
import solve.styles.SeparatorStylesheet
import solve.styles.Style
import solve.utils.createHGrowHBox
import solve.utils.createPxValue
import solve.utils.imageViewIcon
import solve.utils.loadResourcesImage
import solve.utils.materialfx.mfxCircleButton
import solve.utils.materialfx.mfxRangeSlider
import tornadofx.*

class FilterPanelView : View() {
    private val filterPanelFieldsView: FilterPanelFieldsView by inject()

    override val root = vbox {
        mfxRangeSlider(1.0, 19.0, 4.0, 8.5)
        separator {
            addStylesheet(SeparatorStylesheet::class)
        }
        hbox {
            minHeight = HeaderMinHeight

            label("Filters") {
                style {
                    fontFamily = Style.font
                    fontSize = createPxValue(HeaderFontSize)
                    textFill = Paint.valueOf(Style.headerFontColor)
                }

                paddingLeft = 15.0
            }
            add(createHGrowHBox())
            val addButtonIconImage = loadResourcesImage(IconsFiltersAddPath)
            val addButtonGraphic = if (addButtonIconImage != null) {
                imageViewIcon(addButtonIconImage, FilterFieldButtonsSize)
            } else {
                null
            }
            hbox {
                mfxCircleButton(addButtonGraphic, AddFilterButtonSize)
                paddingRight = 14.0
                paddingTop = -6.0
            }

            paddingTop = 10.0
        }
        add(filterPanelFieldsView)
    }

    companion object {
        private const val HeaderMinHeight = 48.0
        private const val HeaderFontSize = 14.0

        private const val FilterFieldButtonsSize = 24.0
        private const val AddFilterButtonSize = 15.0
    }
}
