package solve.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.MultiValue
import tornadofx.Stylesheet
import tornadofx.cssproperty

class ListViewStylesheet : Stylesheet() {

    companion object {
        val fxTreeTableCellBorderColor by cssproperty<MultiValue<Paint>>("-fx-table-cell-border-color")

        val backgroundColour: Color = Color.valueOf(Style.backgroundColour)
        val surfaceColor: Color = Color.valueOf(Style.surfaceColor)
        val primaryColor: Color = Color.valueOf(Style.primaryColor)
    }

    init {
        listView {
            backgroundColor += surfaceColor
        }

        listCell {
            backgroundColor += surfaceColor
        }
    }
}