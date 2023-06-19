package solve.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import solve.constants.IconsFiltersOrCheckBoxPath
import solve.utils.addBackgroundImage
import solve.utils.createCssBoxWithValue
import solve.utils.createLinearUnitsBoxWithLeft
import solve.utils.createPxBoxWithValue
import solve.utils.createPxValue
import tornadofx.*

class FilterPanelListViewStylesheet : Stylesheet() {
    init {
        selected {
            mfxCheckbox {
                rippleContainer {
                    box {
                        borderColor += createCssBoxWithValue(Color.TRANSPARENT)
                        addBackgroundImage(IconsFiltersOrCheckBoxPath)
                    }
                }
            }
        }

        mfxCheckbox {
            rippleContainer {
                box {
                    borderColor += createCssBoxWithValue(Paint.valueOf(Style.SeparatorLineColor))
                    borderWidth += createPxBoxWithValue(CheckBoxBorderWidth)

                    mark {
                        visibility = FXVisibility.HIDDEN
                    }
                }
            }
        }

        mfxCheckListView {
            maxHeight = createPxValue(ListMaxHeight)
            backgroundColor += Color.TRANSPARENT

            virtualFlow {
                backgroundColor += Color.TRANSPARENT

                mfxCheckListCell {
                    backgroundColor += Color.TRANSPARENT
                    borderColor += createCssBoxWithValue(Color.TRANSPARENT)
                    padding = createLinearUnitsBoxWithLeft(4.0, Dimension.LinearUnits.px)

                    dataLabel {
                        fontSize = createPxValue(ListFieldFontSize)
                        fontFamily = Style.Font
                        textFill = Paint.valueOf(Style.ListFontColor)
                    }
                }
            }
        }
    }

    companion object {
        private val mfxCheckListView by cssclass()
        private val mfxCheckListCell by cssclass()
        private val dataLabel by cssclass()
        private val mfxCheckbox by cssclass()
        private val rippleContainer by cssclass()

        private const val ListMaxHeight = 180.0
        private const val CheckBoxBorderWidth = 2.5
        private const val ListFieldFontSize = 14.0
    }
}
