package solve.utils.materialfx.stylesheets

import javafx.scene.paint.Paint
import solve.styles.Style
import solve.utils.createCssBoxWithValue
import tornadofx.*

class MFXValidationTextFieldStylesheet : Stylesheet() {
    init {
        invalid {
            textFill = Paint.valueOf(ErrorBorderColor)
        }

        focusWithin {
        }

        mfxTextField {
            mfxMain.value += Paint.valueOf(DefaultBorderColor)
            borderColor += createCssBoxWithValue(Paint.valueOf(DefaultBorderColor))
        }
    }

    companion object {
        const val ErrorBorderColor = Style.errorColor
        const val DefaultBorderColor = Style.primaryColor

        val mfxTextField by cssclass()
        val invalid by csspseudoclass()
        val mfxMain by cssproperty<MultiValue<Paint>>("-mfx-main")

        val focusWithin by csspseudoclass("focus-within")
    }
}