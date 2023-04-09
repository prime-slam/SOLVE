package solve.utils

import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXTextField
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.attachTo

fun EventTarget.mfxButton(text: String = "", graphic: Node? = null, op: MFXButton.() -> Unit = {}) = MFXButton(text).attachTo(this, op) {
    if (graphic != null) it.graphic = graphic
}

fun EventTarget.mfxTextField(text: String = "", graphic: Node? = null, op: MFXTextField.() -> Unit = {}) = MFXTextField(text).attachTo(this, op)