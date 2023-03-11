package solve

import javafx.scene.control.ScrollPane
import javafx.scene.paint.Paint
import tornadofx.*
import javafx.scene.paint.Color.*

class LightTheme : Stylesheet() {
    companion object {
        val menuBarButton by cssclass()
        val changeButton by cssclass()
        val backgroundBase by cssclass()
        val backgroundElement by cssclass()
        val fxTreeTableCellBorderColor by cssproperty<MultiValue<Paint>> ("-fx-table-cell-border-color")

        const val mainFont = "roboto"
        val mainColor = valueOf("eff0f0")
        val elementColor = WHITE
        val selectedCellColor = valueOf("ababab")
        val hoverCellColor = LIGHTGRAY

    }

    init {


        s(label, button, checkBox, radioButton) {
            fontFamily = mainFont
            fontSize = 15.px
        }


        title{
            fontFamily = mainFont
        }



        listCell {
            textFill = BLACK
            and(hover){
                backgroundColor += hoverCellColor
            }
            and(selected){
                backgroundColor  += selectedCellColor
            }

            backgroundColor += mainColor
        }

        listView{
            and(focused){
                backgroundColor += TRANSPARENT
            }
        }

        menuBarButton {
            backgroundRadius += box(0.px)
            backgroundColor += elementColor
            and(hover) {
                backgroundColor += hoverCellColor

            }
        }
        changeButton {
            backgroundRadius += box(7.px)

        }

        backgroundBase {
            backgroundColor += mainColor
            borderWidth += box(0.px)
            borderColor += box(
                top = mainColor,
                right = mainColor,
                left = mainColor,
                bottom = mainColor
            )

        }

        backgroundElement {
            backgroundColor += elementColor

        }

        treeTableCell{
            textFill = BLACK
        }


       treeTableRowCell{
           arrow{
               backgroundColor += BLACK
           }

           fxTreeTableCellBorderColor.value += TRANSPARENT
           backgroundColor += mainColor
            and(selected){
                backgroundColor += mainColor
            }
           and(hover){
               backgroundColor += hoverCellColor
               and(empty){
                   backgroundColor += mainColor
               }
           }
       }

        treeTableView {
            hBarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            scrollBar{
                thumb{
                    backgroundColor += hoverCellColor
                }
            }

            backgroundColor += mainColor
            columnHeaderBackground {
                maxHeight = 0.px
                prefHeight = 0.px
                minHeight = 0.px
            }
        }




    }
}