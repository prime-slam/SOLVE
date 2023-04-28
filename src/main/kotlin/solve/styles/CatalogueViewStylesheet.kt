package solve.styles

import javafx.scene.paint.Paint
import tornadofx.*

class CatalogueViewStylesheet : Stylesheet() {

    val segmentedButton by cssclass()

    init {

        segmentedButton {
            toggleButton {
                backgroundColor += Paint.valueOf(Style.surfaceColor)
                borderColor += box(Paint.valueOf(Style.primaryColor))
                borderRadius += box(4.px)
                prefWidth = 80.px
                prefHeight = 30.px

                and(selected) {
                    backgroundColor += Paint.valueOf(Style.primaryColor)
                    textFill = Paint.valueOf(Style.surfaceColor)
                }
            }
        }

    }
}