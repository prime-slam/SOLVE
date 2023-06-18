package solve.styles

import javafx.scene.text.FontSmoothingType
import tornadofx.*

class ApplicationStylesheet : Stylesheet() {
    init {
        text {
            fontSmoothingType = FontSmoothingType.GRAY
        }
    }
}
