package solve.styles

import javafx.scene.control.ScrollPane
import javafx.scene.paint.Color
import javafx.scene.paint.Color.BLACK
import javafx.scene.paint.Color.TRANSPARENT
import javafx.scene.paint.Color.WHITE
import javafx.scene.paint.Color.valueOf
import javafx.scene.paint.Paint
import tornadofx.*
import java.net.URI

class TreeTableViewStylesheet : Stylesheet() {
    init {
        treeTableCell {
            textFill = BLACK
            prefHeight = 35.px
        }

        treeTableRowCell {
            arrow {
                backgroundImage += URI("/icons/importer/list_cell_arrow.png")

                prefHeight = 12.px
                prefWidth = 7.px
                backgroundColor += TRANSPARENT
            }
            fxTreeTableCellBorderColor.value += TRANSPARENT
            backgroundColor += SurfaceColor
            and(hover) {
                backgroundColor += BackgroundColor
                and(empty) {
                    backgroundColor += SurfaceColor
                }
            }
        }

        treeTableView {
            borderColor += box(WHITE)
            hBarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            scrollBar {
                s(incrementArrow, decrementArrow) {
                    backgroundColor += TRANSPARENT
                }
                s(incrementButton, decrementButton) {
                    visibility = FXVisibility.HIDDEN
                }

                prefWidth = 10.0.px
                backgroundColor += SurfaceColor
                thumb {
                    backgroundColor += PrimaryColor
                }
            }
            and(focused) {
                borderWidth += box(0.px, 0.px, 0.px, 0.px)
            }

            backgroundColor += SurfaceColor
            columnHeaderBackground {
                borderColor += box(WHITE)
                maxHeight = 0.px
                prefHeight = 0.px
                minHeight = 0.px
            }
        }
    }

    companion object {
        val fxTreeTableCellBorderColor by cssproperty<MultiValue<Paint>>("-fx-table-cell-border-color")

        val BackgroundColor: Color = valueOf(Style.BackgroundColor)
        val SurfaceColor: Color = valueOf(Style.SurfaceColor)
        val PrimaryColor: Color = valueOf(Style.PrimaryColor)
    }
}
