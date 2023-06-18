package solve.styles

import tornadofx.Stylesheet
import tornadofx.c

class TooltipStyle : Stylesheet() {
    init {
        Companion.tooltip {
            fontFamily = Style.FontCondensed
            fontSize = Style.TooltipFontSize
            backgroundColor += c(Style.SurfaceColor)
            textFill = c(Style.TooltipColor)
        }
    }
}
