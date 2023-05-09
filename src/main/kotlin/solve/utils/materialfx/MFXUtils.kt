package solve.utils.materialfx

import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXCheckListView
import io.github.palexdev.materialfx.controls.MFXCheckbox
import io.github.palexdev.materialfx.controls.MFXContextMenu
import io.github.palexdev.materialfx.controls.MFXContextMenuItem
import io.github.palexdev.materialfx.controls.MFXTextField
import io.github.palexdev.materialfx.validation.Constraint
import io.github.palexdev.materialfx.validation.Severity
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import org.controlsfx.control.RangeSlider
import solve.styles.Style
import solve.utils.createPxValue
import solve.utils.materialfx.stylesheets.MFXRangeSliderStylesheet
import solve.utils.materialfx.stylesheets.MFXValidationTextFieldStylesheet
import tornadofx.*
import java.util.function.Supplier

fun EventTarget.mfxButton(
    text: String = "",
    graphic: Node? = null,
    op: MFXButton.() -> Unit = {}
) = MFXButton(text).attachTo(this, op) {
    if (graphic != null) it.graphic = graphic
}

fun EventTarget.mfxTextField(
    text: String = "",
    op: MFXTextField.() -> Unit = {}
) = MFXTextField(text).attachTo(this, op)

fun Node.mfxContextMenu(op: MFXContextMenu.() -> Unit = {}): MFXContextMenu {
    val contextMenu = MFXContextMenu.Builder.build(this).installAndGet()
    op(contextMenu)
    return contextMenu
}

fun MFXContextMenu.item(
    name: String,
    op: MFXContextMenuItem.() -> Unit = {}
) = MFXContextMenuItem(name).also {
    op(it)
    this.addItems(it)
}

fun MFXContextMenu.lineSeparator() = this.addLineSeparator(MFXContextMenu.Builder.getLineSeparator())

fun MFXContextMenuItem.action(op: () -> Unit) = this.setOnAction { op() }

fun EventTarget.mfxCircleButton(
    graphic: Node? = null,
    radius: Double = 20.0,
    text: String = "",
    op: MFXButton.() -> Unit = {}
) = mfxButton(text, graphic) {
    this.graphic = graphic
    Platform.runLater {
        val rippleCenter = boundsInLocal
        rippleGenerator.clipSupplier = Supplier {
            return@Supplier Circle(rippleCenter.centerX, rippleCenter.centerY, radius)
        }
    }
    style {
        backgroundColor += Color.TRANSPARENT
        minHeight = Dimension(radius, Dimension.LinearUnits.px)
    }
    op()
}

fun EventTarget.mfxCheckbox(text: String? = null, op: MFXCheckbox.() -> Unit = {}): MFXCheckbox {
    val mfxCheckbox = MFXCheckbox(text)
    mfxCheckbox.attachTo(this, op)

    return mfxCheckbox
}

fun <T> EventTarget.mfxCheckListView(
    items: ObservableList<T> = observableListOf(),
    op: MFXCheckListView<T>.() -> Unit = {}
): MFXCheckListView<T> {
    val mfxCheckListView = MFXCheckListView(items)
    mfxCheckListView.attachTo(this, op)

    return mfxCheckListView
}

fun EventTarget.mfxRangeSlider(
    min: Double,
    max: Double,
    lowValue: Double,
    highValue: Double,
    op: RangeSlider.() -> Unit = {}
): RangeSlider {
    val slider = RangeSlider(min, max, lowValue, highValue)
    slider.addStylesheet(MFXRangeSliderStylesheet::class)
    slider.attachTo(this, op)

    return slider
}

val MFXTextField.validationMessage: String?
    get() {
        val constraints = validator.validate()
        if (constraints.isEmpty()) {
            return null
        }

        return constraints.first().message
    }

fun EventTarget.mfxIntegerTextField(
    notIntegerErrorMessage: String,
    op: MFXTextField.() -> Unit = {}
) = vbox {
    val mfxTextField = mfxTextField {
        addStylesheet(MFXValidationTextFieldStylesheet::class)
        val areAllDigitsSymbols = textProperty().booleanBinding { text -> text?.all { it.isDigit() } ?: false }
        validator.constraint(Constraint(Severity.ERROR, notIntegerErrorMessage, areAllDigitsSymbols))

        fun enableBorderColorCssString(hexColor: String) =
            "${MFXValidationTextFieldStylesheet.mfxMain.name}: #$hexColor;\n"

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
    }
    label {
        style {
            textFill = Paint.valueOf(Style.errorColor)
            fontFamily = Style.font
            fontSize = createPxValue(10.0)
        }
        visibleProperty().bind(!mfxTextField.validator.validProperty())
        mfxTextField.textProperty().onChange {
            text = mfxTextField.validationMessage ?: return@onChange
        }

        paddingTop = 4.0
    }
    mfxTextField.op()
}
