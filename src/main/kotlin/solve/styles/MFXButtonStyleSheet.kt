package solve.styles

import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*
import java.awt.Insets
import java.net.URI

class MFXButtonStyleSheet : Stylesheet() {
    val mfxButton by cssclass()

    init{
        mfxButton{
            and(hover, pressed){
                backgroundColor += Paint.valueOf(Style.backgroundColour)

            }
            and(pressed){
                backgroundColor += Paint.valueOf(Style.surfaceColor)
            }
       }
    }


}