package solve.styles

import javafx.scene.paint.Paint
import tornadofx.*

class MFXButtonStyleSheet : Stylesheet() {
    private val mfxButton by cssclass()

    init {
        mfxButton {
            and(hover, pressed) {
                backgroundColor += Paint.valueOf(Style.backgroundColor)
            }
            and(pressed) {
                backgroundColor += Paint.valueOf(Style.surfaceColor)
            }
        }
    }
}
