package solve.utils.materialfx

import io.github.palexdev.materialfx.controls.MFXTextField
import io.github.palexdev.materialfx.validation.Constraint
import io.github.palexdev.materialfx.validation.Severity
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.paint.Paint
import solve.styles.Style
import solve.utils.createPxValue
import solve.utils.materialfx.stylesheets.MFXValidationTextFieldStylesheet
import tornadofx.*

class MFXIntegerTextField(private val invalidErrorMessage: String? = null) : MFXTextField() {
    private lateinit var errorMessageLabel: Label

    val root: Node
        get() = vbox {
            removeFromParent()

            add(this@MFXIntegerTextField)
            add(errorMessageLabel)
        }

    init {
        initializeIntegerTextField()
    }

    private fun enableBorderColorCssString(hexColor: String) =
        "${MFXValidationTextFieldStylesheet.mfxMain.name}: #$hexColor;\n" +
            "-fx-border-color: #$hexColor;\n"

    private fun initializeIntegerTextField() {
        addStylesheet(MFXValidationTextFieldStylesheet::class)
        val areAllDigitSymbols = textProperty().booleanBinding { text -> text?.all { it.isDigit() } ?: false }
        validator.constraint(Constraint(Severity.ERROR, invalidErrorMessage, areAllDigitSymbols))

        val enableErrorBorderColorCss = enableBorderColorCssString(MFXValidationTextFieldStylesheet.ErrorBorderColor)
        val enableDefaultBorderColorCss =
            enableBorderColorCssString(MFXValidationTextFieldStylesheet.DefaultBorderColor)
        textProperty().onChange {
            updateInvalid(this, !isValid)
            style += if (isValid) {
                enableDefaultBorderColorCss
            } else {
                enableErrorBorderColorCss
            }
        }

        errorMessageLabel = label {
            style {
                textFill = Paint.valueOf(Style.ErrorColor)
                fontFamily = Style.Font
                fontSize = createPxValue(10.0)
            }
            visibleProperty().bind(!validator.validProperty())
            this@MFXIntegerTextField.textProperty().onChange {
                text = validationMessage ?: return@onChange
            }

            paddingTop = 4.0
        }
    }

    companion object {
        fun EventTarget.mfxIntegerTextField(
            notIntegerErrorMessage: String? = null,
            op: MFXTextField.() -> Unit = {}
        ): MFXIntegerTextField {
            val mfxIntegerTextField = MFXIntegerTextField(notIntegerErrorMessage)
            mfxIntegerTextField.attachTo(this, op)

            return mfxIntegerTextField
        }
    }
}
