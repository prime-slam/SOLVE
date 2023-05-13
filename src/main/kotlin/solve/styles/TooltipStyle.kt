package solve.styles

import tornadofx.Stylesheet
import tornadofx.c

class TooltipStyle : Stylesheet() {
    init {
        Companion.tooltip {
            fontFamily = Style.fontCondensed
            fontSize = Style.tooltipFontSize
            backgroundColor += c(Style.surfaceColor)
            textFill = c(Style.tooltipColor)
        }
    }
}
