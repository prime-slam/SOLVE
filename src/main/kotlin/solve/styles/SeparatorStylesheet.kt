package solve.styles

import javafx.scene.paint.Paint
import solve.utils.createPxBoxWithValue
import tornadofx.*

class SeparatorStylesheet : Stylesheet() {
    init {
        Companion.separator {
            backgroundColor += Paint.valueOf(Style.SeparatorLineColor)
            Companion.line {
                borderWidth += createPxBoxWithValue(0.0)
            }
        }
    }
}
