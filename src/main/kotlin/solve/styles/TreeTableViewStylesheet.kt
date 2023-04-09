package solve.styles

import javafx.scene.control.ScrollPane
import javafx.scene.paint.Color.*
import javafx.scene.paint.Paint
import tornadofx.*

class TreeTableViewStylesheet : Stylesheet() {
    companion object {
        val fxTreeTableCellBorderColor by cssproperty<MultiValue<Paint>>("-fx-table-cell-border-color")
        val background = valueOf("eff0f0")
        val surfaceColor = WHITE
        val primaryColorLight = valueOf("B0BEC5")
        val primaryColor = valueOf("78909C")
    }

    init {
        treeTableCell {

            textFill = BLACK
            prefHeight = 35.px
        }

        treeTableRowCell {
            arrow {
                prefHeight = 12.px
                prefWidth = 7.px
                backgroundColor += primaryColorLight
            }

            fxTreeTableCellBorderColor.value += TRANSPARENT
            backgroundColor += surfaceColor
            and(hover) {
                backgroundColor += background
                and(empty) {
                    backgroundColor += surfaceColor
                }
            }
        }

        treeTableView {
            borderColor += box(WHITE)
            hBarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            scrollBar {
                arrow{
                    visibility = FXVisibility.HIDDEN
                }
                prefWidth = 10.0.px
                backgroundColor += surfaceColor
                thumb {
                    backgroundColor += primaryColor
                }
            }
            and(focused) {
                borderWidth += box(0.px, 0.px, 0.px, 0.px)

            }

            backgroundColor += surfaceColor
            columnHeaderBackground {
                borderColor += box(WHITE)
                maxHeight = 0.px
                prefHeight = 0.px
                minHeight = 0.px
            }
        }
    }
}