package solve

import javafx.scene.control.ScrollPane
import javafx.scene.paint.Paint
import tornadofx.*
import javafx.scene.paint.Color.*

class DarkTheme: Stylesheet() {

    companion object {
        val menuBarButton by cssclass()
        val changeButton by cssclass()
        val backgroundBase by cssclass()
        val backgroundElement by cssclass()
        val scene by cssclass()
        val fxTreeTableCellBorderColor by cssproperty<MultiValue<Paint>> ("-fx-table-cell-border-color")

        const val mainFont = "roboto"
        val mainColor = valueOf("272727")
        val mainTextColor = valueOf("e8e4d9")
        val elementColor = valueOf("393d3f")
        val selectedCellColor = valueOf("ababab")
        val hoverCellColor = LIGHTGRAY
    }

    init {
        s(label, button, checkBox, radioButton) {
            fontFamily = mainFont
            textFill = mainTextColor
            fontSize = 15.px
        }

        button{
            backgroundColor += GRAY
            hover{
                backgroundColor += WHITE
            }
        }


        title {
            fontFamily = mainFont
        }

        listCell {
            textFill = mainTextColor
            and(hover) {
                backgroundColor += elementColor
            }
            and(selected) {
               backgroundColor += BLACK
            }

            backgroundColor += mainColor
        }

        listView {
            backgroundColor += mainColor
            and(focused) {
                backgroundColor += TRANSPARENT
            }
        }

        menuBarButton {
            backgroundRadius += box(0.px)
            backgroundColor += elementColor
            and(hover) {
                backgroundColor += mainColor

            }
        }
        changeButton {
            backgroundRadius += box(7.px)

        }

        backgroundBase {
            backgroundColor += mainColor
            borderWidth += box(5.px)
            borderColor += box(mainColor)

        }

        backgroundElement {
            backgroundColor += elementColor
            borderWidth += box(0.px)
            borderColor += box(mainColor)
        }

        treeTableCell {
            textFill = mainTextColor
        }


        treeTableRowCell {
            arrow {
                backgroundColor += mainTextColor
            }

            fxTreeTableCellBorderColor.value += TRANSPARENT
            backgroundColor += elementColor
            and(hover) {
                backgroundColor += mainColor
                and(empty) {
                    backgroundColor += mainColor
                }
            }
        }

        scrollBar {
            backgroundColor += elementColor
            thumb {
                backgroundColor += LIGHTGRAY
                incrementArrowButton{
                    padding = box(0.px, 0.px,0.px, 0.px)
                }
            }
        }

        treeTableView {
            backgroundColor += elementColor
            s(focused, selected){
                borderColor += box(RED)
                borderWidth += box(0.px)
            }
            hBarPolicy = ScrollPane.ScrollBarPolicy.NEVER


            columnHeaderBackground {
                maxHeight = 0.px
                prefHeight = 0.px
                minHeight = 0.px
            }
        }

        separator {
            line{
                backgroundColor += mainColor
                borderColor += box(TRANSPARENT)
            }

        }

        scene{
                insets(0.0, 0.0)
                borderWidth += box(1.px)
                backgroundColor += mainColor

        }

        alert{
            backgroundColor += mainColor
        }
    }
}