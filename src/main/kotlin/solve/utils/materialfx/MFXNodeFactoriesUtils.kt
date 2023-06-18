package solve.utils.materialfx

import io.github.palexdev.materialfx.controls.MFXButton
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import solve.styles.Style
import solve.styles.Style.ButtonStyle
import tornadofx.*

const val ControlButtonWidth = 75.0
const val ControlButtonHeight = 23.0

fun EventTarget.controlButton(
    text: String,
    width: Double = ControlButtonWidth,
    height: Double = ControlButtonHeight,
    op: MFXButton.() -> Unit = {}
) = mfxButton(text) {
    prefWidth = width
    prefHeight = height
    style = ButtonStyle

    attachTo(this@controlButton, op)
}

fun EventTarget.dialogHeaderLabel(text: String, op: Label.() -> Unit = {}) = label(text) {
    hgrow = Priority.ALWAYS
    style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.HeaderFontSize}"

    attachTo(this@dialogHeaderLabel, op)
}
