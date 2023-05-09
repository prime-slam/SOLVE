package solve.styles

import javafx.scene.text.FontPosture
import tornadofx.Stylesheet
import tornadofx.c

class TooltipStyle : Stylesheet() {
    init {
        Companion.tooltip {
            fontStyle = FontPosture.REGULAR
            fontFamily = Style.fontCondensed
            fontSize = Style.tooltipFontSize
            backgroundColor += c(Style.surfaceColor)
            textFill = c(Style.tooltipColor)
        }
    }
}
